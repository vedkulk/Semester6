import java.util.Scanner;
class Info {
		public String studentName;
		public double studentMarks[];
		public int rollNo;
		Info(){
			this.studentName="";
			this.studentMarks=new double[5];		
			this.rollNo=0;
		}
		public void takeInput() {
			Scanner scan = new Scanner(System.in);
			System.out.println("Enter Student name: ");
			String studentName = scan.nextLine();
			for(int i =0;i<5;i++) {
				System.out.println("Enter marks for subject "+(i+1)+": ");
				studentMarks[i]=scan.nextDouble();
			}
			System.out.println("Enter roll number: ");
			rollNo=scan.nextInt();
		}
		public float calculateAverage() {
			float avgMarks=0;
			for(int i =0;i<5;i++) {
				avgMarks+=studentMarks[i];
			}
			avgMarks/=5;
			return avgMarks;
		}
		public char getGrade() {
			double marks = calculateAverage();
			if(marks>80)
			{
				return 'A';
			}
			else if(marks>50 && marks<80) {
				return 'B';
			}
			return 'C';
		}
}
public class student{
	public static void main(String[] args) {
	
		Info student1 = new Info();
		student1.takeInput();
		System.out.println("Average marks: "+(student1.calculateAverage()));
		System.out.println("Grade: "+student1.getGrade());
	}
}

