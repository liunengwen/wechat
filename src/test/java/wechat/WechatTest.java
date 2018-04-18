package wechat;


import java.util.Arrays;
import java.util.List;



public class WechatTest {
	
	
	public static void main(String[] args) {
		String [] str = new String[]{"you","wu"};
		List<String> list = Arrays.asList(str);
		str[0]= "haha";
		System.out.println(list.get(0)+""+list.get(1));
	}
	
	
}
