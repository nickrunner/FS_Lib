package dev.firstseed.sports_reference;

public interface DownloadListener {
    void onDownloadComplete(AbstractSeason season);
    void onDownloadStatus(int total, int remaining);
}
