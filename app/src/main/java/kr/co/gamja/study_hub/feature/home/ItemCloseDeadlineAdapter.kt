package kr.co.gamja.study_hub.feature.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.gamja.study_hub.R
import kr.co.gamja.study_hub.data.model.Content
import kr.co.gamja.study_hub.data.model.HotStudyPostResponse
import kr.co.gamja.study_hub.databinding.HomeItemCloseDeadlineBinding
import kr.co.gamja.study_hub.feature.toolbar.bookmark.OnItemClickListener

class ItemCloseDeadlineAdapter(private val context:Context):RecyclerView.Adapter<ItemCloseDeadlineAdapter.ItemCloseDeadlineHolder>() {
    var studyPosts: HotStudyPostResponse? = null
    private lateinit var mOnItemClickListener: OnItemClickListener // 북마크 viewModel에 interface 선언
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemCloseDeadlineAdapter.ItemCloseDeadlineHolder {
        val binding =HomeItemCloseDeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemCloseDeadlineHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemCloseDeadlineAdapter.ItemCloseDeadlineHolder,
        position: Int
    ) {
        val studyPostsItem = studyPosts?.content?.get(position)
        holder.setPosts(studyPostsItem)
    }

    override fun getItemCount(): Int {
        return studyPosts?.content?.size ?: 0
    }
    inner class ItemCloseDeadlineHolder(val binding: HomeItemCloseDeadlineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setPosts(studyItem: Content?) {
            val postId:Int? = studyItem?.postId
            studyItem?.let {
                binding.txtHead.text=it.title
                binding.sentencePeople.text=context.getString(R.string.sentence_people,it.remainingSeat)
                val full=it.person-it.remainingSeat
                binding.txtPeople.text=context.getString(R.string.txt_people,full,it.person)
                //TODO("사진추가")


                //북마크 추가
                binding.btnBookmark.setOnClickListener (object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        // 북마크 추가
                        if (binding.btnBookmark.tag.toString() == "0") {
                            binding.btnBookmark.tag = "1"
                            binding.btnBookmark.setBackgroundResource(R.drawable.baseline_bookmark_24_selected)
                            val tagId =binding.btnBookmark.tag.toString()
                            mOnItemClickListener.onItemClick(tagId,postId)

                        } else { // 북마크 삭제
                            binding.btnBookmark.setTag("0")
                            binding.btnBookmark.setBackgroundResource(R.drawable.baseline_bookmark_border_24_unselected)
                            val tagId =binding.btnBookmark.tag.toString()
                            mOnItemClickListener.onItemClick(tagId,postId)
                        }
                    }
                })
            }
        }
    }
}