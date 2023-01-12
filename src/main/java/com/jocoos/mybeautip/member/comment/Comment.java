package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import com.jocoos.mybeautip.global.vo.Files;
import com.jocoos.mybeautip.member.Member;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.jocoos.mybeautip.global.code.UrlDirectory.VIDEO_COMMENT;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;
import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;
import static com.jocoos.mybeautip.member.comment.Comment.CommentState.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "comments")
public class Comment extends MemberAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long postId;

    @Column
    private Long videoId;

    @Column
    private Boolean locked = false;

    @Column(nullable = false)
    private String comment;

    @Column
    private String originalComment;

    @Column
    private Long parentId;

    @Column
    private int commentCount;

    @Column
    private int likeCount;

    @Column
    private int reportCount;

    /**
     * 0: Default, 1: Deleted 2: Blinded, 4: Blinded by Admin
     */
    @Column
    private int state;

    @Column
    private String file;

    @Column(nullable = false)
    @LastModifiedDate
    private Date modifiedAt;

    public void setState(CommentState commentState) {
        this.state = commentState.value();
    }

    public CommentState getCommentState() {
        return getType(this.state);
    }

    public boolean isCommentSameOrLongerThan(int length) {
        return StringUtils.trimAllWhitespace(this.comment).length() >= length;
    }

    public boolean isParent() {
        return parentId == null;
    }

    public boolean isNormal() {
        return DEFAULT.equals(getType(state));
    }

    public Long getMemberId() {
        return createdBy.getId();
    }

    public CommentState getStateString() {
        return getType(state);
    }

    public ZonedDateTime getCreatedAtUTCZoned() {
        return toUTCZoned(createdAt);
    }

    public void hide(boolean isHide) {
        this.state = isHide ? BLINDED_BY_ADMIN.value : DEFAULT.value;
    }

    public boolean eqState(CommentState state) {
        return this.state == state.value;
    }

    public boolean isChild() {
        return parentId != null;
    }

    public void edit(String editComment, Files editFiles, Member editMember) {
        validOnlyOneFile(editFiles);
        validEditAuth(editMember);

        String editFilename = editFiles.getUploadFilename(file);
        validContents(editComment, editFilename);
        this.comment = editComment;
        this.file = editFilename;
    }

    public String getFileUrl() {
        return ImageUrlConvertUtil.toUrl(file, VIDEO_COMMENT, id);
    }

    private void validEditAuth(Member editMember) {
        if (Role.isAdmin(editMember)) {
            validAdminWrite();
            return;
        }
        validSameWriter(editMember);
    }

    private void validSameWriter(Member editMember) {
        if (!createdBy.getId().equals(editMember.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED, "This is not yours.");
        }
    }

    private void validAdminWrite() {
        if (!Role.isAdmin(createdBy)) {
            throw new BadRequestException(ACCESS_DENIED, "Only Comment Written By Admin is Deletable");
        }
    }

    private void validOnlyOneFile(Files files) {
        if (file != null && files.isSingleUpload()) {
            throw new BadRequestException("comment already had file. delete needed");
        }
    }

    private void validContents(String contents, String file) {
        if (isBlank(contents) && isBlank(file)) {
            throw new BadRequestException("Content And File must not be empty.");
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum CommentState implements CodeValue {
        DEFAULT(0, "정상"),
        DELETED(1, "삭제"),
        BLINDED(2, ""),
        BLINDED_BY_ADMIN(4, "어드민 숨김");

        private final int value;
        private final String description;

        public static CommentState getType(int value) {
            CommentState state = DEFAULT;
            for (CommentState v : CommentState.values()) {
                if (v.value() == value) {
                    state = v;
                    break;
                }
            }

            return state;
        }

        public int value() {
            return this.value;
        }

        @Override
        public String getName() {
            return this.name();
        }
    }
}

