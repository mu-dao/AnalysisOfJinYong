package pageRank;

public class PageRankDriver {
	private static int times = 20;
	public static void main(String[] args) throws Exception{
		//pagerankԤ����
		String[] forItr = { "", ""};
		forItr[0] = args[0];
		forItr[1] = args[1] + "/Data0";
		PrePageRank.main(forItr);
		
		//pagerank�㷨������times��
		String[] pagerank = { "", ""};
		for(int i = 0; i < times; i++){
			pagerank[0] = args[1] + "/Data" + i;
			pagerank[1] = args[1] + "/Data" + String.valueOf(i + 1);
			PageRank.main(pagerank);
		}
		//��pagerank�����С��������
		String[] forRv = { args[1] + "/Data" + times, args[1] + "/FinalRank" };
		Sort.main(forRv);
	}
}
