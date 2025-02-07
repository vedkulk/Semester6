import java.util.Scanner;

class palindrome {
	public String s1;
	Scanner scan = new Scanner(System.in);
	palindrome(){
		s1="";
	}
	public void input() {
		System.out.println("Enter a string: ");
		s1=scan.nextLine();
	}
	public boolean checkPalindrome() {
		int n= s1.length();
		int k=n-1;
		for(int i =0;i<n;i++) {	
			if(s1.charAt(i)!=s1.charAt(k)) return false;
			k--;
		}
		return true;
	}
}
class uppercase{
	public String name;
	Scanner scan = new Scanner(System.in);
	uppercase(){
		name="";
	}
	public void input() {
		System.out.println("Enter name in lowercase: ");
		name=scan.nextLine();
	}
	public void result() {
		System.out.println("Name: "+(name.substring(0,1).toUpperCase())+(name.substring(1)));
	}
};
class factorial{
	int f, num;
	Scanner scan = new Scanner(System.in);
	factorial(){
		f=1;
		num=1;
	}
	public void input() {
		System.out.println("Enter number: ");
		num=scan.nextInt();
	}
	public int fact() {
		if(num==0||num==1) {
			f=1;
		}
		else {
		for(int i=2;i<=num;i++) {
			f*=i;
			}
		}
	return f;
	}
}
class pattern {
	int num;
	Scanner scan = new Scanner(System.in);
	public void input() {
		System.out.println("Enter number: ");
		num=scan.nextInt();
	}
	void pattern() {
		for(int i=0;i<num;i++) {
			for(int j=0;j<num-i;j++) {
				System.out.print("  ");
			}
			int times = 2*i+1;
			for(int k=0;k<times;k++) {
				System.out.print("* ");
			}
			System.out.println();
		}
	}
}
public class Assignment1 {
	public static void main(String[] args) {
		uppercase u1= new uppercase();
		u1.input();
		u1.result();
		palindrome p1 = new palindrome();
		p1.input();
		if(p1.checkPalindrome()) { 
			System.out.println("Yes");
		}
		else{
			System.out.println("No");
		}
		factorial f = new factorial();
		f.input();
		System.out.println("Factroial: "+f.fact());
		pattern p = new pattern();
		p.input();
		p.pattern();
	}
}
