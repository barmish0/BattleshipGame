package com.example.battleshipgame.logic;
public class SpecialPowers {private int points;public int getPoints(){return points;}public void addHit(){points++;}public boolean spend(int cost){if(points<cost)return false;points-=cost;return true;}}
