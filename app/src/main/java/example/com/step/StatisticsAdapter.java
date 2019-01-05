package example.com.step;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {
    private static final String TAG = StatisticsAdapter.class.getSimpleName();

    private List<StatisticsModel> statistics;
    private String targetSteps;

    public StatisticsAdapter(final List<StatisticsModel> statistics, String targetSteps) {
        this.statistics = statistics;
        this.targetSteps = targetSteps;
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View viewStatistics = inflater.inflate(R.layout.item_statistics, viewGroup, false);
        return new StatisticsViewHolder(viewStatistics);
    }

    @Override
    public void onBindViewHolder(@NonNull final StatisticsViewHolder statisticsViewHolder, final int i) {
        if (statistics.get(i) != null) {
            Context context = statisticsViewHolder.itemView.getContext();

            //setting the date, if date == null ---> set current date
            Long dateMills = statistics.get(i).getDate() == null ?
                    System.currentTimeMillis() : statistics.get(i).getDate();
            Date date = new Date(dateMills);
            String format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date);
            statisticsViewHolder.mDate.setText(format);

            // set the amount of walk steps
            int walk = statistics.get(i).getWalk() == null ?
                    0 : statistics.get(i).getWalk();
            statisticsViewHolder.mWalk.setText(String.valueOf(walk));

            // set the amount of aerobic steps
            int aerobic = statistics.get(i).getAerobic() == null ?
                    0 : statistics.get(i).getAerobic();
            statisticsViewHolder.mAerobic.setText(String.valueOf(aerobic));

            // set the amount of run steps
            int run = statistics.get(i).getRun() == null ?
                    0 : statistics.get(i).getRun();
            statisticsViewHolder.mRun.setText(String.valueOf(run));

            int sumOfAllWaysOfSteps = walk + aerobic + run;

            // set the actual and the target steps
            String result = context.getResources()
                    .getString(R.string.actual_and_target_steps, sumOfAllWaysOfSteps, targetSteps);
            statisticsViewHolder.mAmountSteps.setTypeface(null, Typeface.NORMAL);
            statisticsViewHolder.mAmountSteps.setText(result);

            // set a progress with animation
            ObjectAnimator objectAnimator = ObjectAnimator
                    .ofInt(statisticsViewHolder.mProgressSteps, "progress", 0, walk);
            ObjectAnimator objectAnimator1 = ObjectAnimator
                    .ofInt(statisticsViewHolder.mProgressSteps, "secondaryProgress", walk, walk + aerobic);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(objectAnimator, objectAnimator1);
            set.setDuration(3000);
            set.setInterpolator(new DecelerateInterpolator());
            set.start();
            statisticsViewHolder.mProgressSteps.setMax(sumOfAllWaysOfSteps);

            // do not display if amount of actual steps of the day less than target
            if (sumOfAllWaysOfSteps < Integer.valueOf(targetSteps)) {
                statisticsViewHolder.mParentForGoalReached.setVisibility(View.GONE);
            } else {
                float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
                float dimension = statisticsViewHolder.mGoalReached.getTextSize() / scaledDensity + 1;

                //animation on textView : Goal reached
                ObjectAnimator animator = ObjectAnimator
                        .ofFloat(statisticsViewHolder.mGoalReached, "textSize", 0, dimension)
                        .setDuration(3000);
                animator.start();

                // animation of icon
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
                statisticsViewHolder.mStarImage.startAnimation(animation);
            }
        }
    }

    @Override
    public int getItemCount() {
        return statistics == null ? 0 : statistics.size();
    }

    class StatisticsViewHolder extends RecyclerView.ViewHolder {
        TextView mDate;
        TextView mAmountSteps;

        ProgressBar mProgressSteps;

        TextView mWalk;
        TextView mAerobic;
        TextView mRun;

        LinearLayout mParentForGoalReached;
        ImageView mStarImage;
        TextView mGoalReached;

        public StatisticsViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDate = itemView.findViewById(R.id.time);
            mAmountSteps = itemView.findViewById(R.id.amount_of_steps);

            mProgressSteps = itemView.findViewById(R.id.progress_walk);

            mWalk = itemView.findViewById(R.id.walk_steps);
            mAerobic = itemView.findViewById(R.id.aerobic_steps);
            mRun = itemView.findViewById(R.id.run_steps);

            mParentForGoalReached = itemView.findViewById(R.id.lin_lay_for_winner);
            mStarImage = itemView.findViewById(R.id.star);
            mGoalReached = itemView.findViewById(R.id.goal_reached_view);
        }
    }
}
