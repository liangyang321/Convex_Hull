import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * create point object to store x,y values
 * @author liang
 *
 */
class Point{
	double x;
	double y;
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
}



public class convex_hull {
	 public static void main(String args[]) throws IOException {
		 List<Point> point_list = new ArrayList<Point>();
		 //need to put file under file path
		 for (int x = 1; x < 7;x++) {
		 File file = new File("case"+ x +".txt");
		 BufferedReader br = new BufferedReader(new FileReader(file));
		 String line;
		 int size =0;
		 while((line = br.readLine()) != null) {
			 String[] arr = line.split(" ");
			 int a = Integer.parseInt(arr[0]);
			 int b = Integer.parseInt(arr[1]);
			 Point p = new Point(a,b);
			 point_list.add(p);
			 size++;
		 }
		 //we convert list to array for sort later
		 Point[] arr = new Point[size];
		 for (int i =0; i< size; i++) {
			 arr[i] = new Point(point_list.get(i).x,point_list.get(i).y);
		 }
//		 System.out.println("points read in ");
//		 for (Point p: arr) {
//			 System.out.print(p.x);
//			 System.out.print(" "+ p.y);
//			 System.out.println();
//		 }
		 Stack<Point> ans = new Stack<>();
		 ans = Convex_hull(arr,size);
		 
		 System.out.println("-----------------");
		 for (Point p: ans) {
			 System.out.print(p.x);
			 System.out.print(" "+p.y);
			 System.out.println();
		 }
		 
		 System.out.println("Case :" + x +" end here" );
		 }
	 }
	 
	 /**
	  * find the convex_hull
	  * @param point_list
	  * @param size
	  */
	 private static Stack<Point> Convex_hull(Point[] arr, int size) {
		 //find left most point and swap it with index 0
		 find_left_most(arr);
		 
//		 System.out.println("after swap left corner point to index 0");
//		 for (Point p: arr) {
//			 System.out.print(p.x);
//			 System.out.print(" "+p.y);
//			 System.out.println();
//		 }
		 
		 merge_sort(arr,arr[0],1,size-1);
//		 
//		 System.out.println("after sort based on index 0");
//		 for (Point p: arr) {
//			 System.out.print(p.x);
//			 System.out.print(" "+p.y);
//			 System.out.println();
//		 }
		 
		 //after sort, we start to find point
		 
		 Stack<Point> s = new Stack<Point>();
		 s.push(arr[0]);
		 s.push(arr[1]);
		 for (int i = 2; i < arr.length; i++) {
			 Point top = s.peek();
			 Point next_top = s.get(s.size() -2);
			 while(s.size() >= 2 && left_turn(next_top,top,arr[i]) <=0) {
				 s.pop();
				 //update top and next_top after pop
				 top = s.peek();
				 if (s.size() >= 2) {
					 next_top = s.get(s.size()-2);
				 }
			 }
			 s.push(arr[i]);
		 }
		 return s;
	 }
	 
	 /** implements O(n_logn) sort
	  * sort point array based on left corner point
	  * @param arr
	  */
	 private static void merge_sort(Point[] arr, Point left_corner,int left, int right) {	
		if (left < right) {
			int mid = (left+right)/2;
			merge_sort(arr,left_corner,left,mid);
			merge_sort(arr,left_corner,mid+1,right);
			merge(arr,left_corner,left,mid,right);
		}
	 }
	 
	 /**
	  * called by merge sort used to merge points
	  * @param arr
	  * @param left_corner
	  * @param left
	  * @param mid
	  * @param right
	  */
	 private static void merge(Point[] arr, Point left_corner, int left, int mid, int right) {
		 int size_left = mid-left+1;
		 int size_right = right-mid;
		 
		 Point[] l = new Point[size_left];
		 Point[] r = new Point[size_right];
		 
		 for (int  m = 0; m< size_left; m++) {
			 l[m] = arr[left+m];
		 }
		 
		 for (int n =0; n < size_right;n++) {
			 r[n] = arr[mid+1+n];
		 }
		 
		 //tow pointer point to left and right array
		 int first = 0;
		 int second = 0;
		 
		 //first index of merged sub_array
		 int k = left; 
		 while(first <size_left && second < size_right ) {
			 if (compare(arr[0],l[first],r[second]) <= 0) { //a < b
				 arr[k] = l[first];
				 first++;
			 }
			 else {
				 arr[k] = r[second];
				 second++;
			 }
			 k++;
		 }
		 while(first < size_left) {
			 arr[k] = l[first];
			 k++;
			 first++;
		 }
		 while(second < size_right) {
			 arr[k] = r[second];
			 k++;
			 second++;
		 }
	 }

	/**
	  * return 0 if collinear, -1 right turn, 1 left turn
	  * @param a
	  * @param b
	  * @param c
	  * @return
	  */
	 private static int left_turn(Point a, Point b, Point c) {
		 double res = (b.x-a.x)*(c.y -a.y)-(c.x-a.x)*(b.y-a.y);
		 if (res == 0) return 0; //collinear
		 return res<0? -1:1;     //-1 right turn, 1 left turn
	 }
	 
	 /**
	  * based on left_corner point, >0 : a>b, <0 : a<b, =0 : a=b
	  * @param a
	  * @param b
	  * @return
	  */
	 private static int compare (Point left_corner, Point a, Point b){
		 //check polar angle by use left_turn check
		 double angle = left_turn(left_corner, a, b);
		 //if equal, check distance
		 if (angle == 0) return (int) (distance(left_corner,a) - distance(left_corner,b));
		 
		 else return angle>0? -1:1;
	 }
	 
	 /**
	  * return the distance between two point
	  * @param a
	  * @param b
	  * @return
	  */
	 private static double distance(Point a, Point b) {
		 double ans =  Math.sqrt((b.y-a.y)*(b.y-a.y) + (b.x-a.x)*(b.x-a.x));
		 return ans;
	 }
	 

	 
	/**
	  * find the left corner point and swap in index 0
	  * @param point_list
	  * @return
	  */
	 private static void find_left_most(Point[] arr) {
		 double y_min = arr[0].y;
		 int min = 0;
		 for (int i = 1; i < arr.length;i++) {
			 double curr_y = arr[i].y;
			 if (curr_y < y_min || (curr_y ==y_min && arr[i].x<arr[min].x )) {
				 min = i;
				 //update min_y every time
				 y_min = arr[min].y;
			 }
		 }
		 //swap left corner point into first index
		 swap(arr,0,min);
	 }
	 private static void swap(Point[] arr, int i, int j) {
		 Point temp = arr[i];
		 arr[i] = arr[j];
		 arr[j] = temp;
	 }
}