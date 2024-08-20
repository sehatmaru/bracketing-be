package xcode.bracketing.domain.response

import lombok.Getter
import lombok.Setter
import org.springframework.http.HttpStatus
import xcode.bracketing.shared.ResponseCode

@Getter
@Setter
class BaseResponse<T> {
    private var statusCode = 0
    private var message: String? = null
    private var result: T? = null

    fun setSuccess(data: T) {
        this.statusCode = HttpStatus.OK.value()
        this.message = ResponseCode.SUCCESS_MESSAGE
        this.result = data
    }

    fun setNotFound() {
        this.statusCode = HttpStatus.NOT_FOUND.value()
        this.message = ResponseCode.NOT_FOUND_MESSAGE
    }

    fun setFailed(message: String) {
        this.statusCode = HttpStatus.BAD_REQUEST.value()
        this.message = ResponseCode.FAILED_MESSAGE + ": " + message
    }

    fun setInvalidToken() {
        this.statusCode = HttpStatus.UNAUTHORIZED.value()
        this.message = ResponseCode.TOKEN_ERROR_MESSAGE
    }

    fun setWrongAuth() {
        this.statusCode = HttpStatus.UNAUTHORIZED.value()
        this.message = ResponseCode.AUTH_ERROR_MESSAGE
    }

    fun setWrongParams() {
        this.statusCode = HttpStatus.BAD_REQUEST.value()
        this.message = ResponseCode.PARAMS_ERROR_MESSAGE
    }

    fun setInvalidPassword() {
        this.statusCode = HttpStatus.UNAUTHORIZED.value()
        this.message = ResponseCode.INVALID_PASSWORD
    }

    fun setExistData() {
        this.statusCode = HttpStatus.CONFLICT.value()
        this.message = ResponseCode.EXIST_MESSAGE
    }

    fun setInvalidMethod(message: String?) {
        this.statusCode = HttpStatus.METHOD_NOT_ALLOWED.value()
        this.message = message
    }

    fun setServerError(message: String?) {
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
        this.message = message
    }
}