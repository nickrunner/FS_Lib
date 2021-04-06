package dev.firstseed.sports_reference.cbb;

public interface OnReferenceDataReadyListener
{
    void onSeasonReady(int year, Season teams);
    void statusUpdate(int total, int remaining);
}
