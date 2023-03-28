package com.example.demo;

public class FindPageDto {
    private String keyword;
    private Integer pageSize;
    private Integer pageNum;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getKeyword() {
        return keyword;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public FindPageDto(String keyword, Integer pageSize, Integer pageNum) {
        this.keyword = keyword;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }
}
