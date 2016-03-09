package ua.nau.edu.Support.View;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class Animation {
    public static class Reveal {

        public static int animDuration = 400;

        /* Center of View
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
         */

        public static void revealOpenTopRight(final View view) {
            int cx = view.getRight();
            int cy = view.getTop();

            // get the final radius for the clipping circle
            int dx = Math.max(cx, view.getWidth() - cx);
            int dy = Math.max(cy, view.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(animDuration);
            view.setVisibility(View.VISIBLE);

            try {
                animator.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public static void revealCloseTopRight(final View view) {
            int cx = view.getRight();
            int cy = view.getTop();

            // get the final radius for the clipping circle
            int dx = Math.max(cx, view.getWidth() - cx);
            int dy = Math.max(cy, view.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(animDuration);
            animator = animator.reverse();

            try {
                animator.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.INVISIBLE);
                }
            }, animDuration);
        }
    }

}
