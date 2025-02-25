// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.display_cutout;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.support.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import org.chromium.blink.mojom.ViewportFit;
import org.chromium.chrome.browser.ChromeActivity;
import org.chromium.chrome.browser.InsetObserverView;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.chrome.browser.tab.TabObserver;
import org.chromium.testing.local.LocalRobolectricTestRunner;

/**
 * Tests for {@link DisplayCutoutController} class.
 */
@RunWith(LocalRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DisplayCutoutControllerTest {
    @Mock
    private Tab mTab;

    @Captor
    private ArgumentCaptor<TabObserver> mTabObserverCaptor;

    @Mock
    private ChromeActivity mChromeActivity;

    @Mock
    private InsetObserverView mInsetObserver;

    private DisplayCutoutController mDisplayCutoutController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Mock dependency on InsetObserverView.
        when(mTab.getActivity()).thenReturn(mChromeActivity);
        when(mChromeActivity.getInsetObserverView()).thenReturn(mInsetObserver);

        mDisplayCutoutController = spy(new DisplayCutoutController(mTab));
    }

    @Test
    @SmallTest
    public void testViewportFitUpdate() throws Throwable {
        verify(mDisplayCutoutController, never()).maybeUpdateLayout();

        mDisplayCutoutController.setViewportFit(ViewportFit.COVER);
        verify(mDisplayCutoutController).maybeUpdateLayout();
    }

    @Test
    @SmallTest
    public void testViewportFitUpdateNotChanged() throws Throwable {
        verify(mDisplayCutoutController, never()).maybeUpdateLayout();

        mDisplayCutoutController.setViewportFit(ViewportFit.AUTO);
        verify(mDisplayCutoutController, never()).maybeUpdateLayout();
    }

    @Test
    @SmallTest
    public void testCutoutModeWhenAutoAndInteractable() throws Throwable {
        when(mTab.isUserInteractable()).thenReturn(true);

        mDisplayCutoutController.setViewportFit(ViewportFit.AUTO);
        Assert.assertEquals("LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT",
                mDisplayCutoutController.getDisplayCutoutMode());
    }

    @Test
    @SmallTest
    public void testCutoutModeWhenCoverAndInteractable() throws Throwable {
        when(mTab.isUserInteractable()).thenReturn(true);

        mDisplayCutoutController.setViewportFit(ViewportFit.COVER);
        Assert.assertEquals("LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES",
                mDisplayCutoutController.getDisplayCutoutMode());
    }

    @Test
    @SmallTest
    public void testCutoutModeWhenContainAndInteractable() throws Throwable {
        when(mTab.isUserInteractable()).thenReturn(true);

        mDisplayCutoutController.setViewportFit(ViewportFit.CONTAIN);
        Assert.assertEquals("LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER",
                mDisplayCutoutController.getDisplayCutoutMode());
    }

    @Test
    @SmallTest
    public void testCutoutModeWhenAutoAndNotInteractable() throws Throwable {
        mDisplayCutoutController.setViewportFit(ViewportFit.AUTO);
        Assert.assertEquals("LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT",
                mDisplayCutoutController.getDisplayCutoutMode());
    }

    @Test
    @SmallTest
    public void testCutoutModeWhenCoverAndNotInteractable() throws Throwable {
        mDisplayCutoutController.setViewportFit(ViewportFit.COVER);
        Assert.assertEquals("LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT",
                mDisplayCutoutController.getDisplayCutoutMode());
    }

    @Test
    @SmallTest
    public void testCutoutModeWhenContainAndNotInteractable() throws Throwable {
        mDisplayCutoutController.setViewportFit(ViewportFit.CONTAIN);
        Assert.assertEquals("LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT",
                mDisplayCutoutController.getDisplayCutoutMode());
    }

    @Test
    @SmallTest
    public void testLayoutOnInteractability_True() throws Throwable {
        // In this test we are checking for a side effect of maybeUpdateLayout.
        // This is because the tab observer holds a reference to the original
        // mDisplayCutoutController and not the spied one.
        verify(mTab).addObserver(mTabObserverCaptor.capture());
        reset(mTab);

        mTabObserverCaptor.getValue().onInteractabilityChanged(true);
        verify(mTab).getActivity();
    }

    @Test
    @SmallTest
    public void testLayoutOnInteractability_False() throws Throwable {
        // In this test we are checking for a side effect of maybeUpdateLayout.
        // This is because the tab observer holds a reference to the original
        // mDisplayCutoutController and not the spied one.
        verify(mTab).addObserver(mTabObserverCaptor.capture());
        reset(mTab);

        mTabObserverCaptor.getValue().onInteractabilityChanged(false);
        verify(mTab).getActivity();
    }

    @Test
    @SmallTest
    public void testLayoutOnShown() throws Throwable {
        // In this test we are checking for a side effect of maybeUpdateLayout.
        // This is because the tab observer holds a reference to the original
        // mDisplayCutoutController and not the spied one.
        verify(mTab).addObserver(mTabObserverCaptor.capture());
        reset(mTab);

        mTabObserverCaptor.getValue().onShown(mTab);
        verify(mTab).getActivity();
    }
}
