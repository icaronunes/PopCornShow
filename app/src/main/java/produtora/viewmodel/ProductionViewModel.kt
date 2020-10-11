package produtora.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import domain.Company
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import utils.Api

class ProductionViewModel(override val app: Application, api: Api) : BaseViewModel(app = app) {

	val loadingMedia: ILoadingMedia = LoadingMedia(api)

	private var _company: MutableLiveData<BaseRequest<Company>> = MutableLiveData()
	val company: LiveData<BaseRequest<Company>> = _company

	fun fetchCompanyData(id: Int, pager: Int) {
		loadingMedia.fetchCompany(id, pager, _company = _company)
	}


}
