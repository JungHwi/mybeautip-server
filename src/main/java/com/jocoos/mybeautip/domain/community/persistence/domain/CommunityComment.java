package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.Files;
import com.jocoos.mybeautip.member.Member;
import lombok.*;

import javax.persistence.*;

import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.DELETE;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY_COMMENT;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.StringUtils.hasText;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "community_comment")
public class CommunityComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long categoryId;

    @Column
    private long communityId;

    @Column(name = "member_id")
    private long memberId;

    @Column
    private Long parentId;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunityStatus status;

    @Column
    private String contents;

    @Column
    private int likeCount;

    @Column
    private int commentCount;

    @Column
    private int reportCount;

    @Column
    private String file;

    @ManyToOne
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    public CommunityComment delete() {
        if (!this.status.isDeletable()) {
            throw new BadRequestException("This status can not delete. This Community Status is " + this.status);
        }
        this.status = DELETE;
        return this;
    }

    public void edit(String editedContents, Files files, Member editor) {
        validIsSingleFile(files);
        validEditAuth(editor);

        String editedFilename = files.getUploadFilename(file);
        validContents(editedContents, editedFilename);
        this.contents = editedContents;
        this.file = editedFilename;
    }

    public void valid() {
        validContents(this.contents, this.file);
    }

    public boolean isCommentSameOrLongerThan(int length) {
        return hasText(contents) && contents.length() >= length;
    }

    public boolean isParent() {
        return parentId == null;
    }

    public boolean isChild() {
        return parentId != null;
    }

    public boolean eqStatus(CommunityStatus status) {
        return this.status == status;
    }

    public String getFileUrl() {
        return toUrl(file, COMMUNITY_COMMENT, id);
    }

    public boolean containFile() {
        return file != null;
    }

    private void validIsSingleFile(Files files) {
        if (containFile() && files.isSingleUpload()) {
            throw new BadRequestException("comment already had file. delete needed");
        }
    }

    private void validEditAuth(Member editor) {
        if (Role.isAdmin(editor)) {
            validAdminWrite();
            return;
        }
        validSameWriter(editor);
    }

    private void validSameWriter(Member editor) {
        if (!member.getId().equals(editor.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED, "This is not yours.");
        }
    }

    private void validAdminWrite() {
        if (!Role.isAdmin(member)) {
            throw new BadRequestException(ACCESS_DENIED, "Only Comment Written By Admin is Deletable");
        }
    }

    private void validContents(String contents, String file) {
        if (isBlank(contents) && isBlank(file)) {
            throw new BadRequestException("Content And File must not be empty.");
        }
    }
}
