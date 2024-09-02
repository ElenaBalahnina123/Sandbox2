package com.slobozhaninova.heroapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.slobozhaninova.heroapp.databinding.HeroDetailFragmentBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HeroDetailFragment : Fragment(R.layout.hero_detail_fragment) {

    @Inject
    lateinit var assistedFactory: HeroDetailVMAssistedFactory

    private val viewModel by viewModels<HeroDetailViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(
                    HeroDetailFragmentArgs.fromBundle(requireArguments())
                ) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HeroDetailFragmentBinding.bind(view).run {
            viewModel.stateFlow.onEach {
                when (it) {
                    is HeroDetailViewModelState.Hero -> {
                        toolbar.title = it.heroListItem.name
                        textViewHeroName.text = it.heroListItem.id.toString()
                    }
                    HeroDetailViewModelState.None -> {
                        toolbar.title = "not found"
                        textViewHeroName.text = "---"
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }
}

@AssistedFactory
interface HeroDetailVMAssistedFactory {
    fun create(args: HeroDetailFragmentArgs): HeroDetailViewModel
}

class HeroDetailViewModel @AssistedInject constructor(
    private val heroRepository: HeroRepository,
    @Assisted args: HeroDetailFragmentArgs,
) : ViewModel() {

    private val mutableStateFlow =
        MutableStateFlow<HeroDetailViewModelState>(HeroDetailViewModelState.None) //по умолчанию значение

    val stateFlow: StateFlow<HeroDetailViewModelState> get() = mutableStateFlow.asStateFlow()
    //mutableStateFlow изменяемый, stateFlow неизменяемый. при вызове asStateFlow создается новый StateFlow, который нельзя изменить снаружи

    init {
        viewModelScope.launch {
            heroRepository.getHeroId(args.heroId)?.let {
                Log.d("hero detail", "hero found: $it")
                mutableStateFlow.value = HeroDetailViewModelState.Hero(it)
            }
        }
    }
}


sealed class HeroDetailViewModelState { //что-то типо этапов
    object None : HeroDetailViewModelState()

    @Suppress("UNCHECKED_CAST")
    data class Hero(
        val heroListItem: HeroListItem
    ) : HeroDetailViewModelState()
}