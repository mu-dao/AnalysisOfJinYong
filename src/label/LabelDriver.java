package label;

public class LabelDriver {
	private static int times = 10;
	public static void main(String[] args) throws Exception{
		//��ǩ����Ԥ����
		String[] forItr = { "", "", ""};
		forItr[0] = args[0];
		forItr[1] = args[1] + "/Data0";
		forItr[2] = args[2];
		PreLabel.main(forItr);
		//��ǩ�����㷨������times��
		String[] label = { "", ""};
		for(int i = 0; i < times; i++){
			label[0] = args[1] + "/Data" + i;
			label[1] = args[1] + "/Data" + String.valueOf(i + 1);
			Label.main(label);
		}
		String[] forRv = { args[1] + "/Data" + times, args[1] + "/Final" };
		Trim.main(forRv);
	}
}