package com.mobile.app.bomber.common.base;

import androidx.lifecycle.ViewModel;

import com.mobile.app.bomber.data.repository.AdRepository;
import com.mobile.app.bomber.data.repository.CommentRepository;
import com.mobile.app.bomber.data.repository.MovieSearchRepository;
import com.mobile.app.bomber.data.repository.MsgRepository;
import com.mobile.app.bomber.data.repository.TikSearchRepository;
import com.mobile.app.bomber.data.repository.UploadRepository;
import com.mobile.app.bomber.data.repository.UserRepository;
import com.mobile.app.bomber.data.repository.VersionRepository;
import com.mobile.app.bomber.data.repository.VideoRepository;

import javax.inject.Inject;

public abstract class MyBaseViewModel extends ViewModel {

    @Inject
    protected AdRepository adRepository;

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected VideoRepository videoRepository;

    @Inject
    protected UploadRepository uploadRepository;

    @Inject
    protected CommentRepository commentRepository;

    @Inject
    protected MsgRepository msgRepository;

    @Inject
    protected TikSearchRepository tikSearchRepository;

    @Inject
    protected MovieSearchRepository movieSearchRepository;

    @Inject
    protected VersionRepository versionRepository;
}
