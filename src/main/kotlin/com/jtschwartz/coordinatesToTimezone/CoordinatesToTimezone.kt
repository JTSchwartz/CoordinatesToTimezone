@file:Suppress("unused")

package com.jtschwartz.coordinatesToTimezone

import com.google.cloud.functions.*
import mu.KotlinLogging
import java.io.IOException

class CoordinatesToTimezone : HttpFunction {
	
	private val logger = KotlinLogging.logger {}
	
	@Throws(IOException::class)
	override fun service(request: HttpRequest, response: HttpResponse) {
		logger.info { "hello world" }
		response.writer.write("FUNCTION COMPLETE")
	}
}