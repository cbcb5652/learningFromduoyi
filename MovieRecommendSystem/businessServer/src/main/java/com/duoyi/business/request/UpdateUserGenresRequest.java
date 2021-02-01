package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserGenresRequest {

    private String username;

    private List<String> genres = new ArrayList<>();



}
