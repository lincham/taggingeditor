public class  TrainFileTest
{
	public static void main(String[] args) 
	{
		String s = "-1	The hotel is old need a lot work. You have to pay RMB$50 to access the internet for a day, I have a lot problem to connect, the connection drops every 3-5 minutes, I told the front desk manager about the connection, at the check out time, he ends up charged me RMB$25, the reason was that I have connected for 30 minutes, what a joke if the connection drops in 3-5 mins notthing can be done... The service was bad, this is definitily not a 4 star hotel, not even 2 star. I will never recomand this one to anyone.";
		String[] str = s.split("	");
		System.out.println(str.length);
	}
}