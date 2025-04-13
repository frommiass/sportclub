package pro.grino.karateclub.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс для работы с Google Sheets API через Retrofit
 */
interface GoogleSheetsService {

    @GET("{spreadsheetId}/values/{range}")
    suspend fun getValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String
    ): Response<SheetResponse>

    @POST("{spreadsheetId}/values/{range}:append")
    suspend fun appendValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("valueInputOption") valueInputOption: String = "USER_ENTERED",
        @Query("key") apiKey: String,
        @Body valueRange: ValueRange
    ): Response<AppendResponse>

    @POST("{spreadsheetId}/values/{range}")
    suspend fun updateValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("valueInputOption") valueInputOption: String = "USER_ENTERED",
        @Query("key") apiKey: String,
        @Body valueRange: ValueRange
    ): Response<UpdateResponse>
}

/**
 * Модели данных для работы с API
 */
data class SheetResponse(
    @SerializedName("range") val range: String,
    @SerializedName("majorDimension") val majorDimension: String,
    @SerializedName("values") val values: List<List<String>>
)

data class ValueRange(
    @SerializedName("range") val range: String,
    @SerializedName("majorDimension") val majorDimension: String = "ROWS",
    @SerializedName("values") val values: List<List<String>>
)

data class AppendResponse(
    @SerializedName("spreadsheetId") val spreadsheetId: String,
    @SerializedName("updates") val updates: UpdateResult
)

data class UpdateResponse(
    @SerializedName("spreadsheetId") val spreadsheetId: String,
    @SerializedName("updatedRange") val updatedRange: String,
    @SerializedName("updatedRows") val updatedRows: Int,
    @SerializedName("updatedColumns") val updatedColumns: Int,
    @SerializedName("updatedCells") val updatedCells: Int
)

data class UpdateResult(
    @SerializedName("updatedRange") val updatedRange: String,
    @SerializedName("updatedRows") val updatedRows: Int,
    @SerializedName("updatedColumns") val updatedColumns: Int,
    @SerializedName("updatedCells") val updatedCells: Int
)