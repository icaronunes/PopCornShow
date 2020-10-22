package lista.viewmodel

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.*
import com.google.common.truth.Truth.*
import domain.movie.ListaFilmes
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import loading.api.ILoadingMedia
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.junit.MockitoJUnitRunner
import utils.Api

@RunWith(MockitoJUnitRunner::class)
class ListByTypeViewModelTest {

	@Rule
	@JvmField
	val instantExecutorRule = InstantTaskExecutorRule()

	@MockK
	lateinit var loadingMedia: ILoadingMedia

	@MockK
	lateinit var app: Application

	@MockK
	lateinit var context: Context

	@MockK
	lateinit var liveData: MutableLiveData<BaseRequest<ListaFilmes>>

	private lateinit var api: Api

	private lateinit var listByTypeViewModel: ListByTypeViewModel

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
		api = Api(context)
		listByTypeViewModel = ListByTypeViewModel(app = app, api = api)
	}

	@Test
	fun `de`() {
		val mockedObserver: Observer<BaseRequest<ListaFilmes>> = spyk()
		listByTypeViewModel.movies.observeForever(mockedObserver)
		// val loadingMedia: ILoadingMedia = spyk(LoadingMedia(api))
		every { loadingMedia.getMovieList(any(), "movie", 1) } returns Unit
		loadingMedia.getMovieList(mockedObserver, "movie", 1)
		listByTypeViewModel.fetchListMovies("movie", page = 1)
		// loadingMedia.getMovieListByType(liveData, "movie", 1)
		val slot = slot<BaseRequest<ListaFilmes>>()
		verify { mockedObserver.onChanged(capture(slot)) }

		assertThat(slot.captured).isEqualTo(BaseRequest.Success<ListaFilmes>(any()))
		// every { loadingMedia.getMovieListByType(any(), "movie", 1) } returns Unit
	}
}