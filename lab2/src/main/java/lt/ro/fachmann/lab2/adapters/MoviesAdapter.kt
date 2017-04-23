package lt.ro.fachmann.lab2.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.movie_list_row_even.view.*
import kotlinx.android.synthetic.main.movie_list_row_odd.view.*
import lt.ro.fachmann.lab2.Movie
import lt.ro.fachmann.lab2.R
import lt.ro.fachmann.lab2.snack
import org.jetbrains.anko.imageResource

class MoviesAdapter(private val moviesList: MutableList<Movie>,
                    private val itemClick: (Movie) -> Unit) :
        RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun getItemCount() = moviesList.size

    override fun getItemViewType(position: Int) = position % 2

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) = holder.bindMovie(moviesList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        when (viewType) {
            0 -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_row_even, parent, false)
                return MovieViewHolderEven(itemView, itemClick)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_row_odd, parent, false)
                return MovieViewHolderOdd(itemView, itemClick)
            }
        }
    }

    abstract class MovieViewHolder(itemView: View, val itemClick: (Movie) -> Unit) : RecyclerView.ViewHolder(itemView) {

        open fun bindMovie(movie: Movie) {
            seenIconUpdate(movie.seen)
            itemView.setOnClickListener { itemClick(movie) }
            itemView.setOnLongClickListener {
                flipMovie(movie)
                true
            }
        }

        fun flipMovie(movie: Movie) {
            movie.seen = !movie.seen
            seenIconUpdate(movie.seen)
        }

        fun bindViews(movie: Movie, posterBackground: ImageView, poster: ImageView, title: TextView, genre: TextView, year: TextView, seenIcon: ImageView) = with(movie) {
            posterBackground.imageResource = posterId
            //posterBackground.setRadius(0.5f)
            Picasso.with(posterBackground.context).load(posterId).fit().centerCrop()
                    .transform(BlurTransformation(posterBackground.context, 8)).into(posterBackground)
            poster.imageResource = posterId
            title.text = this.title
            genre.text = this.genre
            year.text = this.year
            seenIcon.setOnClickListener {
                flipMovie(this)
            }
        }

        abstract fun seenIconUpdate(seen: Boolean)
    }

    class MovieViewHolderEven(itemView: View, itemClick: (Movie) -> Unit) : MovieViewHolder(itemView, itemClick) {
        override fun bindMovie(movie: Movie) = with(itemView) {
            super.bindMovie(movie)
            bindViews(movie, posterBackgroundEven, posterEven, titleEven, genreEven, yearEven, seenIconEven)
        }

        override fun seenIconUpdate(seen: Boolean) {
            itemView.seenIconEven.imageResource = if (seen) R.drawable.ic_movie_seen_layer else R.drawable.ic_movie_unseen_layer
        }
    }

    class MovieViewHolderOdd(itemView: View, itemClick: (Movie) -> Unit) : MovieViewHolder(itemView, itemClick) {
        override fun bindMovie(movie: Movie) = with(itemView) {
            super.bindMovie(movie)
            bindViews(movie, posterBackgroundOdd, posterOdd, titleOdd, genreOdd, yearOdd, seenIconOdd)
        }

        override fun seenIconUpdate(seen: Boolean) {
            itemView.seenIconOdd.imageResource = if (seen) R.drawable.ic_movie_seen_layer else R.drawable.ic_movie_unseen_layer
        }
    }

    fun remove(viewHolder: RecyclerView.ViewHolder, recyclerView: RecyclerView) {
        val position = viewHolder.adapterPosition
        val movie = moviesList[position]
        recyclerView.snack(recyclerView.context.getString(R.string.list_removed_movie, movie.title)) {
            setAction(recyclerView.context.getString(R.string.list_removed_undo), {
                moviesList.add(position, movie)
                notifyItemInserted(position)
                notifyDataSetChanged()
            })
        }
        moviesList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }
}