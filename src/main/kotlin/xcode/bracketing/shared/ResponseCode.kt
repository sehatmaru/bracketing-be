package xcode.bracketing.shared

object ResponseCode {
    //ERROR MESSAGE
    const val FAILED_MESSAGE: String = "Bad request"
    const val NOT_FOUND_MESSAGE: String = "Data not found"
    const val PARAMS_ERROR_MESSAGE: String = "Wrong params"
    const val EXIST_MESSAGE: String = "Data exist"
    const val AUTH_ERROR_MESSAGE: String = "Wrong username/password"
    const val TOKEN_ERROR_MESSAGE: String = "Invalid token"
    const val INVALID_PASSWORD: String = "Invalid password"

    //SUCCESS
    const val SUCCESS_MESSAGE: String = "Success"
}