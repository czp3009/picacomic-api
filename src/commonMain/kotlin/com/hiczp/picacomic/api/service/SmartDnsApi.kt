package com.hiczp.picacomic.api.service

import com.hiczp.picacomic.api.model.SmartDnsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

public interface SmartDnsApi {
    @GET("app/picacomic/dns/resolve")
    public suspend fun resolve(
        @Query("domain")
        domain: String,
    ): SmartDnsResponse
}
