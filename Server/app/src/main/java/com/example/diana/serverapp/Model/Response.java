package com.example.diana.serverapp.Model;

import java.util.List;

public class Response {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
