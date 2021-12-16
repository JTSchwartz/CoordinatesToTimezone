@file:Suppress("unused")

package com.jtschwartz.coordinatesToTimezone

import com.google.cloud.functions.*
import com.google.gson.*
import mu.KotlinLogging
import us.dustinj.timezonemap.TimeZoneMap
import java.io.IOException


class CoordinatesToTimezone : HttpFunction {
	private val log = KotlinLogging.logger {}
	
	@Throws(IOException::class)
	override fun service(request: HttpRequest, response: HttpResponse) {
		val responseBody: JsonObject = try {
			val requestBody: JsonObject = Gson().fromJson(request.reader, JsonElement::class.java).asJsonObject
			
			val latitude = requestBody.parseCoordinate("latitude")
			val longitude = requestBody.parseCoordinate("longitude")
			
			TimeZoneMap.forRegion(latitude - 1.0, longitude - 1.0, latitude + 1.0, longitude + 1.0).run {
				JsonObject().apply {
					addProperty("timezone", getOverlappingTimeZone(latitude, longitude)?.zoneId ?: "throw RuntimeException(Invalid coordinates)")
				}
			}
		} catch (e: Exception) {
			val error = if (e is JsonParseException) "Error parsing JSON" else e.message
			log.error(e) { error }
			JsonObject().apply { addProperty("err", error) }
		}
		
		response.writer.write(responseBody.toString())
	}
	
	private fun JsonObject.parseCoordinate(unit: String): Double {
		return if (this.has(unit)) {
			this.get(unit).asString.toDouble()
		} else throw RuntimeException("Missing $unit field")
	}
}