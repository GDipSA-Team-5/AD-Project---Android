package iss.nus.edu.sg.appfiles.mobile_ewaste.network

import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitExtensionsTest {
    @Test
    fun await_returnsBody_whenResponseSuccessful() = runTest {
        val call = FakeCall(response = Response.success("ok"), failure = null)

        val result = call.await()

        assertEquals("ok", result)
    }

    @Test
    fun await_throws_whenResponseUnsuccessful() = runTest {
        val mediaType = "text/plain".toMediaType()
        val errorBody = "bad".toResponseBody(mediaType)
        val call = FakeCall<String>(response = Response.error(400, errorBody), failure = null)

        try {
            call.await()
            throw AssertionError("Expected exception was not thrown")
        } catch (ex: IllegalStateException) {
            assertEquals("Request failed: 400", ex.message)
        }
    }

    @Test
    fun await_throwsOriginalException_onFailure() = runTest {
        val failure = IllegalStateException("boom")
        val call = FakeCall<String>(response = null, failure = failure)

        try {
            call.await()
            throw AssertionError("Expected exception was not thrown")
        } catch (ex: IllegalStateException) {
            assertEquals("boom", ex.message)
        }
    }

    private class FakeCall<T>(
        private val response: Response<T>?,
        private val failure: Throwable?
    ) : Call<T> {
        private var canceled = false

        override fun enqueue(callback: Callback<T>) {
            if (failure != null) {
                callback.onFailure(this, failure)
                return
            }
            callback.onResponse(this, response!!)
        }

        override fun cancel() {
            canceled = true
        }

        override fun isCanceled(): Boolean = canceled

        override fun isExecuted(): Boolean = false

        override fun clone(): Call<T> = FakeCall(response, failure)

        override fun execute(): Response<T> {
            throw UnsupportedOperationException("Not used in tests")
        }

        override fun request(): Request {
            return Request.Builder().url("http://localhost/").build()
        }

        override fun timeout(): Timeout = Timeout.NONE
    }
}
