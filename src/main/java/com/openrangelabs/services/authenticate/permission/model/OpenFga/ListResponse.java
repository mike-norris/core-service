package com.openrangelabs.services.authenticate.permission.model.OpenFga;

import lombok.Data;

import java.util.List;
@Data
public class ListResponse {
    List<TupleKey> tuples;
    String continuation_token;
}
