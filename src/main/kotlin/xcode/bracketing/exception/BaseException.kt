package xcode.bracketing.exception

import org.springframework.beans.ConversionNotSupportedException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.lang.Nullable
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import xcode.bracketing.domain.response.BaseResponse
import xcode.bracketing.shared.ResponseCode

@ControllerAdvice
class BaseExceptions : ResponseEntityExceptionHandler() {
    private val response: BaseResponse<String> = BaseResponse()

    protected fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders?,
        status: HttpStatus?,
        request: WebRequest?
    ): ResponseEntity<Any> {
        ex.message?.let { response.setFailed(it) }

        return ResponseEntity(response, HttpStatus.OK)
    }

    protected fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders?,
        status: HttpStatus?,
        request: WebRequest?
    ): ResponseEntity<Any> {
        response.setInvalidMethod(ex.message)

        return ResponseEntity(response, HttpStatus.OK)
    }

    protected fun handleExceptionInternal(
        ex: Exception,
        @Nullable body: Any?,
        headers: HttpHeaders?,
        status: HttpStatus?,
        request: WebRequest?
    ): ResponseEntity<Any> {
        response.setServerError(ex.message)

        return ResponseEntity(response, HttpStatus.OK)
    }

    protected fun handleConversionNotSupported(
        ex: ConversionNotSupportedException,
        headers: HttpHeaders?,
        status: HttpStatus?,
        request: WebRequest?
    ): ResponseEntity<Any> {
        println(ex)
        response.setServerError(ex.message)

        return ResponseEntity(response, HttpStatus.OK)
    }

    protected fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders?,
        status: HttpStatus?,
        request: WebRequest?
    ): ResponseEntity<Any> {
        println(ex)
        response.setFailed(ex.message)

        return ResponseEntity(response, HttpStatus.OK)
    }

    protected fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders?,
        status: HttpStatus?,
        request: WebRequest?
    ): ResponseEntity<Any> {
        println(ex)
        ex.message?.let { response.setFailed(it) }

        return ResponseEntity(response, HttpStatus.OK)
    }

    @ExceptionHandler(AppException::class)
    fun exception(ex: AppException): ResponseEntity<BaseResponse<String>> {
        when (ex.message) {
            ResponseCode.TOKEN_ERROR_MESSAGE -> {
                response.setInvalidToken()
            }

            ResponseCode.AUTH_ERROR_MESSAGE -> {
                response.setWrongAuth()
            }

            ResponseCode.NOT_FOUND_MESSAGE -> {
                response.setNotFound()
            }

            ResponseCode.EXIST_MESSAGE -> {
                response.setExistData()
            }

            ResponseCode.PARAMS_ERROR_MESSAGE -> {
                response.setWrongParams()
            }

            ResponseCode.INVALID_PASSWORD -> {
                response.setInvalidPassword()
            }

            else -> ex.message?.let { response.setFailed(it) }
        }
        return ResponseEntity<BaseResponse<String>>(response, HttpStatus.OK)
    }
}