package com.me.restaurantsmartsearch.utils;

import com.me.restaurantsmartsearch.R;

/**
 * Created by Laptop88T on 2/9/2017.
 */
public class AnimatorUtils {
    private AnimatorUtils() {
    }

    public static class Animator {
        public int animEnter, animExit, popEnter, popExit;

        public Animator(int animEnter, int animExit, int popEnter, int popExit) {
            this.animEnter = animEnter;
            this.animExit = animExit;
            this.popEnter = popEnter;
            this.popExit = popExit;
        }

    }

    public static Animator getAnimInRightOutLeft() {
        return new Animator(R.animator.slide_in_right, R.animator.slide_out_left,
                R.animator.slide_in_left, R.animator.slide_out_right);
    }

    public static Animator getAnimationBottomToTop() {
        return new Animator(R.animator.slide_in_up, R.animator.slide_out_bottom, R.animator.slide_in_bottom, R.animator.slide_out_up);
    }

}
