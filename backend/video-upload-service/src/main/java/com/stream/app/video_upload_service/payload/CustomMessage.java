package com.stream.app.video_upload_service.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomMessage {

    private String message;

    private Boolean isSuccess;

    private Object Details;


}
