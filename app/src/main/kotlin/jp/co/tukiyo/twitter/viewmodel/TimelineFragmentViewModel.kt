package jp.co.tukiyo.twitter.viewmodel

import android.content.Context
import android.widget.Toast
import com.evernote.android.state.State
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.subjects.BehaviorSubject

class TimelineFragmentViewModel(context: Context) : FragmentViewModel(context) {
    val tweets: BehaviorSubject<Tweet> = BehaviorSubject.create()

    @State
    var latestTweetId: Long? = null

    fun fetchTimeline() {
        val sinceId = latestTweetId?.let { it + 1 }
        Twitter.getApiClient().statusesService.homeTimeline(null, sinceId, null, false, false, false, false)
                .enqueue(object : Callback<List<Tweet>>() {
                    override fun failure(exception: TwitterException?) {
                        Toast.makeText(context, "failed get tweet", Toast.LENGTH_SHORT).show()
                    }

                    override fun success(result: Result<List<Tweet>>?) {
                        result?.data?.run {
                            reversed().forEach { tweets.onNext(it) }
                            firstOrNull()?.let {
                                latestTweetId = it.id
                            }
                        }
                    }
                })
    }
}
