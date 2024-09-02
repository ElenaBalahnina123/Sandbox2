package com.slobozhaninova.heroapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroesListViewModel @Inject constructor(private val heroesRepository: HeroRepository) : ViewModel() {

    private val _state = MutableStateFlow<HeroesState>(HeroesState.Initial)

    val state: StateFlow<HeroesState>
        get() = _state.asStateFlow()



    init {
        loadList()
    }

    fun loadList() {
        if (_state.value is HeroesState.Loading)return
        viewModelScope.launch {
            _state.value = HeroesState.Loading

            _state.value = try {
                HeroesState.HeroesList(
                    heroesRepository.getHeroList()
                )
            } catch (err: Throwable) {
                if (err is CancellationException) throw err
                HeroesState.Error(err)
            }
        }
    }


    sealed class HeroesState {
        object Initial : HeroesState()
        object Loading : HeroesState()
        data class HeroesList(
            val list: List<HeroListItem>
        ) : HeroesState()

        data class Error(
            val error: Throwable
        ) : HeroesState()
    }
}