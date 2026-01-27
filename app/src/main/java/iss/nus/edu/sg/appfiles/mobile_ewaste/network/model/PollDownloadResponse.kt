package iss.nus.edu.sg.appfiles.mobile_ewaste.network.model

data class PollDownloadResponse(
    val code: Int,
    val errMsg: String?,
    val data: PollDownloadData?
)

data class PollDownloadData(
    val url: String?
)
