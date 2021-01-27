package com.hen.tmall_springboot.util;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

public class Page4Navigator<T> implements Serializable {

    Page<T> pageFromJpa;
    int navigatePages;
    int totalPages;
    int number;
    long totalElements;
    int size;
    int numberOfElements;
    List<T> content;
    boolean hasContent;
    boolean first;
    boolean last;
    boolean hasNext;
    boolean hasPrevious;
    int[] navigatepageNums;

    public Page4Navigator() {
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }

    public Page4Navigator(Page<T> pageFromJPA, int navigatePages) {
        this.pageFromJpa = pageFromJPA;
        this.navigatePages = navigatePages;
        this.totalPages = pageFromJPA.getTotalPages();
        this.number = pageFromJPA.getNumber();
        this.totalElements = pageFromJPA.getTotalElements();
        this.size = pageFromJPA.getSize();
        this.numberOfElements = pageFromJPA.getNumberOfElements();
        this.content = pageFromJPA.getContent();
        this.hasContent = pageFromJPA.hasContent();
        this.first = pageFromJPA.isFirst();
        this.last = pageFromJPA.isLast();
        this.hasNext = pageFromJPA.hasNext();
        this.hasPrevious = pageFromJPA.hasPrevious();
        this.calcNavigatepageNums();
    }

    private void calcNavigatepageNums() {
        int navigatepageNums[];
        int totalPages = getTotalPages();
        int num = getNumber();
        //当总页数小于或等于导航页码数时
        if (totalPages <= navigatePages) {
            navigatepageNums = new int[totalPages];
            for (int i = 0; i < totalPages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new int[navigatePages];
            int startNum = num - navigatePages / 2;
            int endNum = num + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > totalPages) {
                endNum = totalPages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        }
        this.navigatepageNums = navigatepageNums;
    }

    public int getNavigatePages() {
        return this.navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return this.totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfElements() {
        return this.numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<T> getContent() {
        return this.content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContent() {
        return this.hasContent;
    }

    public void setHasContent(boolean isHasContent) {
        this.hasContent = isHasContent;
    }

    public boolean isFirst() {
        return this.first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return this.hasNext;
    }

    public void setHasNext(boolean isHasNext) {
        this.hasNext = isHasNext;
    }

    public boolean isHasPrevious() {
        return this.hasPrevious;
    }

    public void setHasPrevious(boolean isHasPrevious) {
        this.hasPrevious = isHasPrevious;
    }

    public int[] getNavigatepageNums() {
        return this.navigatepageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }
}
