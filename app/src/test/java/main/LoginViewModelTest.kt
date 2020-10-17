package main

import android.app.Application
import com.google.common.truth.Truth.assertThat
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import login.LoginViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoginViewModelTest {

	@Rule
	@JvmField
	val instantTaskExecutorRule = InstantTaskExecutorRule()

	@MockK
	private lateinit var application: Application
	private lateinit var model: LoginViewModel

	private val emailWrong = "ppopArrobadaed.br"
	private val emailCorrect = "popcornshow@com.br"
	private val pass = "12345678"

	@Before
	fun setup() {
		MockKAnnotations.init(this)
		model = LoginViewModel(application)
	}

	@Test
	fun `test validade do login enviado - correto`() {
		val status = model.validParamets(emailCorrect, pass, pass)
		assertThat(status).isTrue()
	}

	@Test
	fun `test validade do login enviado - errado`() {
		val status = model.validParamets(emailWrong, pass, pass)
		assertThat(status).isFalse()
	}

	@Test
	fun `test validade do password - correto`() {
		val status = model.validParamets(emailCorrect, pass, pass)
		assertThat(status).isTrue()
	}

	@Test
	fun `test validade do password - errado`() {
		val status = model.validParamets(emailCorrect, pass, pass+"erro")
		assertThat(status).isFalse()
	}

}