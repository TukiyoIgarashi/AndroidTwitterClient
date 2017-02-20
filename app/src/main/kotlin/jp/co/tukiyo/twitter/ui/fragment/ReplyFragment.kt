package jp.co.tukiyo.twitter.ui.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import jp.co.tukiyo.twitter.R
import jp.co.tukiyo.twitter.databinding.FragmentReplayBinding
import jp.co.tukiyo.twitter.extensions.onNext
import jp.co.tukiyo.twitter.extensions.sync
import jp.co.tukiyo.twitter.ui.adapter.TweetListAdapter
import jp.co.tukiyo.twitter.viewmodel.ReplyFragmentViewModel

class ReplyFragment :BaseFragment<FragmentReplayBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_replay
    lateinit var viewModel : ReplyFragmentViewModel
    lateinit var tweetListAdapter: TweetListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ReplyFragmentViewModel(context)
        tweetListAdapter = TweetListAdapter(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            replyList.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = tweetListAdapter
            }
            swipeTweetList.run {
                setOnRefreshListener {
                    viewModel.fetchReply()
                    if (isRefreshing) {
                        isRefreshing = false
                    }
                }
            }
        }

        viewModel.replies.sync()
                .onNext { tweetListAdapter.add(0, it) }
                .subscribe()
                .run { disposables?.add(this) }

        viewModel.fetchReply()
    }
}