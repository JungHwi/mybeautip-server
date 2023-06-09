package com.jocoos.mybeautip.tag;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TagService {
    // Tag category
    public static final int TAG_MEMBER = 1;
    public static final int TAG_VIDEO = 2;
    public static final int TAG_POST = 3;
    public static final int TAG_BANNER = 4;
    public static final int TAG_COMMENT = 5;

    private static final String startSign = "#";
    private static final String regex = "^[\\p{L}\\p{N}_]+";  // letters, numbers, underscore(_)
    private static final int MAX_TAG_LENGTH = 25;

    private final TagRepository tagRepository;
    private final TagHistoryRepository tagHistoryRepository;

    public TagService(TagRepository tagRepository,
                      TagHistoryRepository tagHistoryRepository) {
        this.tagRepository = tagRepository;
        this.tagHistoryRepository = tagHistoryRepository;
    }

    // Save tags without increasing refCount
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void touchRefCount(String text) {
        List<String> tags = parseHashTag(text);
        for (String name : tags) {
            Optional<Tag> optional = tagRepository.findByName(name);
            if (optional.isPresent()) {
                Tag tag = optional.get();
                tag.setModifiedAt(new Date());
                tagRepository.save(tag);
            } else {
                tagRepository.save(new Tag(name, 0));
            }
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void increaseRefCount(String text) {
        List<String> tags = parseHashTag(text);
        for (String name : tags) {
            Optional<Tag> optional = tagRepository.findByName(name);
            if (optional.isPresent()) {
                tagRepository.updateTagRefCount(optional.get().getId(), 1);
            } else {
                tagRepository.save(new Tag(name, 1));
            }
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void decreaseRefCount(String text) {
        List<String> tags = parseHashTag(text);
        for (String name : tags) {
            tagRepository.findByName(name)
                    .ifPresent(tag -> {
                        if (tag.getRefCount() > 0) {
                            tagRepository.updateTagRefCount(tag.getId(), -1);
                        }
                    });
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateRefCount(String oldText, String newText) {
        List<String> oldTagNames = parseHashTag(oldText);
        List<String> newTagNames = parseHashTag(newText);

        List<String> removeTagNames = (List<String>) CollectionUtils.removeAll(oldTagNames, newTagNames);
        List<String> addTagNames = (List<String>) CollectionUtils.removeAll(newTagNames, oldTagNames);

        for (String name : removeTagNames) {
            tagRepository.findByName(name)
                    .ifPresent(tag -> {
                        if (tag.getRefCount() > 0) {
                            tagRepository.updateTagRefCount(tag.getId(), -1);
                        }
                    });
        }

        for (String name : addTagNames) {
            Optional<Tag> optional = tagRepository.findByName(name);
            if (optional.isPresent()) {
                tagRepository.updateTagRefCount(optional.get().getId(), 1);
            } else {
                tagRepository.save(new Tag(name, 1));
            }
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void addHistory(String text, int category, long resourceId, Member me) {
        List<String> uniqueTagNames = parseHashTag(text);
        for (String name : uniqueTagNames) {
            tagRepository.findByName(name).ifPresent(tag -> tagHistoryRepository.findByTagAndCategoryAndResourceIdAndCreatedBy(tag, category, resourceId, me)
                    .orElseGet(() -> tagHistoryRepository.save(new TagHistory(tag, category, resourceId, me))));
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeHistory(String text, int category, long resourceId, Member me) {
        List<String> uniqueTagNames = parseHashTag(text);
        for (String name : uniqueTagNames) {
            tagRepository.findByName(name)
                    .ifPresent(tag -> tagHistoryRepository.findByTagAndCategoryAndResourceIdAndCreatedBy(tag, category, resourceId, me)
                            .ifPresent(tagHistoryRepository::delete));
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeAllHistory(Member me) {
        tagHistoryRepository.findByCreatedBy(me).forEach(tagHistoryRepository::delete);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateHistory(String oldText, String newText, int category, long resourceId, Member me) {
        List<String> oldTagNames = parseHashTag(oldText);
        List<String> newTagNames = parseHashTag(newText);

        List<String> removeTagNames = (List<String>) CollectionUtils.removeAll(oldTagNames, newTagNames);
        List<String> addTagNames = (List<String>) CollectionUtils.removeAll(newTagNames, oldTagNames);

        for (String name : removeTagNames) {
            tagRepository.findByName(name)
                    .ifPresent(t -> tagHistoryRepository.findByTagAndCategoryAndResourceIdAndCreatedBy(t, category, resourceId, me)
                            .ifPresent(tagHistoryRepository::delete));
        }

        for (String name : addTagNames) {
            tagRepository.findByName(name).ifPresent(tag -> tagHistoryRepository.findByTagAndCategoryAndResourceIdAndCreatedBy(tag, category, resourceId, me)
                    .orElseGet(() -> tagHistoryRepository.save(new TagHistory(tag, category, resourceId, me))));
        }
    }

    private List<String> parseHashTag(String text) {
        List<String> tags = new ArrayList<>();
        String delimiters = " \n\r\t"; // space, new line, tab
        StringTokenizer tokenizer = new StringTokenizer(text, delimiters);

        int offset = 0;
        String temp = text;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.startsWith(startSign) || StringUtils.countMatches(token, startSign) > 1) {
                continue;
            }
            token = StringUtils.substringAfter(token, startSign);

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(token);

            if (matcher.find()) {
                String tag = matcher.group();
                if (tag.length() > MAX_TAG_LENGTH) {
                    throw new BadRequestException("Valid tag length is between 1 to 25: " + tag);
                }

                if (StringUtils.removeAll(tag, "_").length() > 0) {
                    offset = offset + temp.indexOf(startSign + tag) + token.length();
                    temp = text.substring(offset);
                    tags.add(tag);
                }
            }
        }

        List<String> uniqueTagNames = new ArrayList<>();
        for (String name : tags) {
            if (!uniqueTagNames.contains(name)) {
                uniqueTagNames.add(name);
            }
        }
        return uniqueTagNames;
    }
}