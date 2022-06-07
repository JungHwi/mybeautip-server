package com.jocoos.mybeautip.restapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@EqualsAndHashCode
@ToString
@Getter
public class CursorResponse<E> {
    private List<E> content;
    private String nextCursor;
    private String nextRef;
    private Integer totalCount;
    private Integer guestCount; // used at watcher list

    public CursorResponse(Builder<E> builder) {
        this.content = builder.content;
        this.nextCursor = builder.nextCursor;
        this.nextRef = builder.nextRef;
        this.totalCount = builder.totalCount;
        this.guestCount = builder.guestCount;
    }

    public static class Builder<E> {
        private List<E> content;
        private String nextCursor;
        private String nextRef;
        private Integer totalCount;
        private Integer guestCount;

        @JsonIgnore
        private String uri;

        @JsonIgnore
        private MultiValueMap<String, String> properties = new LinkedMultiValueMap<>();

        public Builder(String uri, List<E> content) {
            this.uri = uri;
            this.content = content;
        }

        private void createRef() {
            this.nextRef = UriComponentsBuilder.newInstance()
                    .fromUriString(uri).queryParams(properties).build().toUriString();
        }

        public Builder withCategory(String category) {
            if (!StringUtils.isBlank(category)) {
                this.properties.add("category", category);
            }
            return this;
        }

        public Builder withCategory(Integer category) {
            if (category != null) {
                this.properties.add("category", String.valueOf(category));
            }
            return this;
        }

        public Builder withKeyword(String keyword) {
            if (!StringUtils.isBlank(keyword)) {
                this.properties.add("keyword", keyword);
            }
            return this;
        }

        public Builder withType(String type) {
            if (!StringUtils.isBlank(type)) {
                this.properties.add("type", type);
            }
            return this;
        }

        public Builder withSort(String sort) {
            if (!StringUtils.isBlank(sort)) {
                this.properties.add("sort", sort);
            }
            return this;
        }

        public Builder withState(String state) {
            if (!StringUtils.isBlank(state)) {
                this.properties.add("state", state);
            }
            return this;
        }

        public Builder withCount(int count) {
            this.properties.add("count", String.valueOf(count));
            return this;
        }

        public Builder withCursor(String cursor) {
            if (!StringUtils.isBlank(cursor)) {
                this.nextCursor = cursor;
                this.properties.add("cursor", cursor);
            }
            return this;
        }

        public Builder withTotalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder withGuestCount(int guestCount) {
            this.guestCount = guestCount;
            return this;
        }

        public CursorResponse<E> toBuild() {
            String countValue = null;
            if (this.properties.containsKey("count")) {
                countValue = this.properties.getFirst("count");
            }
            int count = (countValue == null) ? 0 : Integer.parseInt(countValue);

            if (this.content.size() >= count && uri != null) {
                createRef();
                log.debug("created nextRef: {}", nextRef);
            } else {
                this.nextCursor = null;
                this.nextRef = null;
            }
            return new CursorResponse<>(this);
        }
    }
}
