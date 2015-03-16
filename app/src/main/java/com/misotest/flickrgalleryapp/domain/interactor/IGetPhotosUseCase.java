package com.misotest.flickrgalleryapp.domain.interactor;

import java.util.List;

public interface IGetPhotosUseCase extends UseCase {

    public void getPhotos(int page, String query);

    public void sendUrisToPresenter(List<String> strings);

    public void stop();

}
