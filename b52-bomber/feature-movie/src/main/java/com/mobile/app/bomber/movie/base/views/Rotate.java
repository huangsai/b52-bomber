package com.mobile.app.bomber.movie.base.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * This transition-v21 captures the rotation property of targets before and after
 * the scene change and animates any changes.
 */
public class Rotate extends Transition {
    private static final String PROP_NAME_ROTATION = "android:rotate:rotation";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_NAME_ROTATION, transitionValues.view.getRotation());
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_NAME_ROTATION, transitionValues.view.getRotation());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final View view = endValues.view;
        float startRotation = (Float) startValues.values.get(PROP_NAME_ROTATION);
        float endRotation = (Float) endValues.values.get(PROP_NAME_ROTATION);
        if (startRotation != endRotation) {
            view.setRotation(startRotation);
            return ObjectAnimator.ofFloat(view, View.ROTATION,
                    startRotation, endRotation);
        }
        return null;
    }
}
