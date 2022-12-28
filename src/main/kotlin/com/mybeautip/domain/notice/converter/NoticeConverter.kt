package com.mybeautip.domain.notice.converter

//@Component
abstract class NoticeConverter {

//    fun converts(request: NoticeCreateRequest): Notice {
//        val noticeFiles = convertsToNoticeFiles(request.files)
//
//        return Notice(title = request.title, description = request.description, files = noticeFiles)
//    }
//
//    fun converts(entity: Notice): NoticeResponse {
//        val noticeResponseList = convertsToFileResponse(entity.fileList)
//        return NoticeResponse(id = entity.id, title = entity.title, description = entity.description, files = noticeResponseList)
//    }
//
//    fun converts(file: FileDto): NoticeFile {
//        val filename = FileUtil.getFileName(file.url)
//
//        return NoticeFile(file = filename)
//    }
//
//    fun convertsToNoticeFiles(files: List<FileDto>): MutableList<NoticeFile> {
//        var result: MutableList<NoticeFile> = mutableListOf()
//        for (file: FileDto in files) {
//            result.add(this.converts(file))
//        }
//
//        return result
//    }
//
//    fun convertsToFileResponse(file: NoticeFile): FileResponse {
//        return FileResponse(type = file.type, file = file.file)
//    }
//
//    fun convertsToFileResponse(files: List<NoticeFile>?): List<FileResponse> {
//        var result: MutableList<FileResponse> = mutableListOf()
//        if (files != null) {
//            for (file: NoticeFile in files) {
//                result.add(this.convertsToFileResponse(file))
//            }
//        }
//        return result
//    }
}