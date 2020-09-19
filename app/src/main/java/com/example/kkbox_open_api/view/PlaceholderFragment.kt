package com.example.kkbox_open_api.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkbox_open_api.*
import com.example.kkbox_open_api.AppInfo.CLIENT_ID
import com.example.kkbox_open_api.AppInfo.CLIENT_SECRET
import com.example.kkbox_open_api.model.PlayListsAPI
import com.example.kkbox_open_api.model.PlayListsRepository
import com.example.kkbox_open_api.model.PlayListsResponse
import com.example.kkbox_open_api.viewModel.PageViewModel
import com.example.kkbox_open_api.viewModel.PlayListsAdaptor
import com.example.kkbox_open_api.viewModel.PlayListsViewModel
import com.example.kkbox_open_api.viewModel.PlayListsViewModelFactory
import com.kkbox.openapideveloper.api.Api
import com.kkbox.openapideveloper.auth.Auth
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private lateinit var playListsViewModel: PlayListsViewModel
    private lateinit var auth: Auth
    private lateinit var accessToken: String
    private lateinit var api: Api
    private lateinit var adapter: PlayListsAdaptor
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        auth = Auth(CLIENT_ID, CLIENT_SECRET, requireContext())
        accessToken = auth.clientCredentialsFlow.fetchAccessToken().get().get("access_token").asString
        api = Api(accessToken, "TW", requireContext())
        val playListsAPI = PlayListsAPI()
        val playListsRepository = PlayListsRepository(playListsAPI)
        playListsViewModel = ViewModelProvider(this, PlayListsViewModelFactory(playListsRepository)).get(PlayListsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        pageViewModel.text.observe(this, Observer<String> {
            textView.text = it
            playListsViewModel.getPlayLists(api, 0, 10, it)
            val recyclerView: RecyclerView = root.findViewById(R.id.viewPlayLists)
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = PlayListsAdaptor(playListsViewModel)
            recyclerView.adapter = adapter

            playListsViewModel.listLiveData.observe(this,
                Observer<ArrayList<PlayListsResponse>> {
                    adapter.list = it
                    recyclerView.post {
                        adapter.notifyDataSetChanged()
                        isLoading = false
                    }
                })

            playListsViewModel.imageLiveData.observe(this,
                Observer<CopyOnWriteArrayList<Bitmap>> {
                    adapter.imageList = it
                    recyclerView.post {
                        adapter.notifyDataSetChanged()
                        isLoading = false
                    }
                })

            playListsViewModel.openPlayListEvent.observe(this, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    val playListId = it
                    val intent = Intent(activity, PlayListActivity::class.java)
                    intent.putExtra("playListId", playListId)
                    startActivity(intent)
                }
            })

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (!isLoading) {
                        if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                            adapter.itemCount - 1) {
                            isLoading = true
                            playListsViewModel.getPlayLists(api, adapter.itemCount, 10, it)
                        }
                    }
                }
            })
        })
        return root
    }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}