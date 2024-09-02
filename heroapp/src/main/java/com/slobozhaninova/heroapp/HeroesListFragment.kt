package com.slobozhaninova.heroapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.slobozhaninova.heroapp.databinding.HeroesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HeroesListFragment : Fragment(R.layout.heroes_fragment) {

    private val viewModel by viewModels<HeroesListViewModel>()

    private val adapter = HeroListAdapter(::onHeroClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state
            .filterIsInstance<HeroesListViewModel.HeroesState.HeroesList>()
            .onEach {
                adapter.submitList(it.list)
            }
            .launchIn(lifecycleScope)
    }

    private fun onHeroClick(item: HeroListItem) {
        findNavController().navigate(
            HeroesListFragmentDirections.actionHeroesListFragmentToHeroDetailFragment(item.id.toLong())
        )

//        parentFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, HeroDetailFragment().apply {
//                arguments = bundleOf(
//                    HeroDetailFragment.ARG_HERO_ID to item.id
//                )
//            })
//            .addToBackStack(null)
//            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HeroesFragmentBinding.bind(view).run {
            heroRv.adapter = adapter

            viewModel.state
                .map { it is HeroesListViewModel.HeroesState.Loading }
                .onEach {
                    srl.isRefreshing = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

            srl.setOnRefreshListener {
                viewModel.loadList()
            }
        }

    }

}


data class HeroListItem(
    val name: String,
    val id: Int,
)

class HeroViewHolder(
    val textView: TextView,
    root: View
) : RecyclerView.ViewHolder(root)

class HeroListAdapter(private val onItemClick: (HeroListItem) -> Unit) :
    ListAdapter<HeroListItem, HeroViewHolder>(object : DiffUtil.ItemCallback<HeroListItem>() {
        override fun areItemsTheSame(oldItem: HeroListItem, newItem: HeroListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HeroListItem, newItem: HeroListItem): Boolean {
            return oldItem == newItem
        }
    }) {

    init {
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view as TextView
        return HeroViewHolder(textView, view)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val item = getItem(position)
        holder.textView.text = getItem(position).name
        holder.itemView.setOnClickListener {
            onItemClick.invoke(item)
        }
    }
}

