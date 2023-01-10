package com.jocoos.mybeautip.domain.notice.persistence.domain;

import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import com.jocoos.mybeautip.global.code.FileOperationType;
import com.jocoos.mybeautip.global.config.jpa.AllBaseEntity;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notice extends AllBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    @Column(nullable = false)
    private Boolean isVisible;

    @Column
    private Boolean isImportant;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<NoticeFile> files;

    public void delete() {
        this.status = NoticeStatus.DELETE;
    }

    public void editTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            return;
        }
        this.title = title;
    }

    public void editDescription(String description) {
        if (StringUtils.isEmpty(description)) {
            return;
        }
        this.description = description;
    }

    public void editVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void editImportant(Boolean isImportant) {
        this.isImportant = isImportant;
    }

    public void addFile(FileDto file) {
        if (file.getOperation() != FileOperationType.UPLOAD) {
            return;
        }

        NoticeFile noticeFile = NoticeFile.builder()
                .type(file.getType())
                .file(file.filename())
                .notice(this)
                .build();

        this.files.add(noticeFile);
    }

    public void addFile(List<FileDto> fileList) {
        fileList.stream()
                .filter(dto -> dto.getOperation().equals(FileOperationType.UPLOAD))
                .forEach(this::addFile);
    }

    public void removeFile(FileDto file) {
        if (file.getOperation() != FileOperationType.DELETE) {
            return;
        }
        files.removeIf(noticeFile -> noticeFile.getFile().equals(FileUtil.getFileName(file.getUrl())));
    }

    public void removeFile(List<FileDto> fileList) {
        fileList.stream()
                .filter(dto -> dto.getOperation().equals(FileOperationType.DELETE))
                .forEach(this::removeFile);
    }

    public void editFiles(List<FileDto> fileList) {
        if (fileList == null) {
            return;
        }

        for (FileDto file : fileList) {
            switch (file.getOperation()) {
                case UPLOAD -> addFile(file);
                case DELETE -> removeFile(file);
            }
        }
        sortFiles(fileList);
    }

    private void sortFiles(List<FileDto> fileList) {
        List<String> wantList = fileList.stream()
                .filter(file -> file.getOperation() != FileOperationType.DELETE)
                .map(FileDto::filename)
                .toList();

        IntStream.range(0, this.files.size())
                .forEach(index -> replaceFileIfDiff(wantList.get(index), this.files.get(index)));
    }

    private void replaceFileIfDiff(String filename, NoticeFile file) {
        if (!file.getFile().equals(filename)) {
            file.editFile(filename);
        }
    }

    @PostPersist
    public void postPersist() {
        if (CollectionUtils.isNotEmpty(files)) {
            files.forEach(file -> file.setNotice(this));
        }
    }
}
