package com.hiczp.picacomic.api

public sealed class PicaException(
    message: String,
    public open val code: Int? = null,
    public open val error: String? = null,
    public open val status: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

public class PicaApiException(
    message: String,
    override val code: Int? = null,
    override val error: String? = null,
    override val status: Int? = null,
    cause: Throwable? = null,
) : PicaException(message, code, error, status, cause)

public class PicaUnauthorizedException(
    message: String,
    override val error: String? = null,
    cause: Throwable? = null,
) : PicaException(message, error = error, status = 401, cause = cause)

public class PicaNotFoundException(
    message: String,
    override val error: String? = null,
    cause: Throwable? = null,
) : PicaException(message, error = error, status = 404, cause = cause)

public class PicaServerException(
    message: String,
    override val status: Int,
    override val error: String? = null,
    cause: Throwable? = null,
) : PicaException(message, error = error, status = status, cause = cause)

public class PicaSignatureException(
    message: String,
    override val code: Int? = null,
    override val status: Int? = null,
    cause: Throwable? = null,
) : PicaException(message, code = code, error = "1023", status = status, cause = cause)

public class PicaMissingDataException(message: String) : PicaException(message)

public class ExternalApiException(public val businessCode: Int, message: String) : Exception(message)

public class ExternalHostExhaustedException(
    message: String = "All external API hosts were exhausted",
) : Exception(message)

public class DirectApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
