package label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Label {
	public static class LabelMapper extends Mapper<Object, Text, Text, Text> {
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			String line = value.toString();
			String[] tuple = line.split("\t");
			String keyName = tuple[0];//����

			if(tuple.length >= 3){
				String label = tuple[1];//#��ǩ,rank#
				String link = tuple[2];//�����������б�
			
				String[] array = tuple[2].split(" | ");
				for (int i = 0; i < array.length; i++){
					String[] tmp = array[i].split(",");//tmp[0] = ������tmp[1] = rank
					if(tmp.length >= 2){
						context.write(new Text(tmp[0]), new Text(label));//keyName����Щ�˶�Ͷ��һ��label
					}
				}
				context.write(new Text(keyName), new Text("%" + link));//��������б�
			}
			
		}
	}

	public static class LabelReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			ArrayList<String> nameList = new ArrayList<>();//��keyͶƱ��������
			HashMap<String, Integer> mapLabel = new HashMap<>();
			HashMap<String, Double> mapRank = new HashMap<>();
			String link = new String();
			
        	for(Text value : values){
        		String label = value.toString();
        		if(label.startsWith("%")){
        			link = value.toString().substring(label.indexOf("%") + 1);//��ȡ�����б�
        			continue;
        		}
        		String[] nameRank = label.substring(1,label.length() - 1).split(",");//ȥ�����˵�#��Ȼ��ͨ���ָ��������rank
        		nameList.add(nameRank[0]);
        		//ͳ��Ͷ��key��ÿ�ֱ�ǩ�ĸ���
        		if(mapLabel.containsKey(nameRank[0])) mapLabel.put(nameRank[0], mapLabel.get(nameRank[0]) + 1);
        		else mapLabel.put(nameRank[0], 1);
        		//�洢��ǩ��rank
        		mapRank.put(nameRank[0],Double.valueOf(nameRank[1]));
        	}
        	int maxCount = 0;
        	String label = new String("");
        	for(String name : nameList){
        		/*
        		 * �����ǩ���ִ����ȵ�ǰ�������󣬸��±�ǩ�����ֵ
        		 * �����ǩ���ִ�����ͬʱ��ȡrankֵ�������Ϊ��ǩ
        		 */
        		if((mapLabel.get(name) > maxCount)||(mapLabel.get(name) == maxCount && mapRank.get(name) > mapRank.get(label))){
            		maxCount = mapLabel.get(name);
            		label = new String(name);
        		}
        	}
        	context.write(key, new Text("#" + label + "," + mapRank.get(label) + "#\t" + link));//key "\t" #��ǩ# "\t" �����б�
		}
	}
    
	public static void main(String[] args) throws Exception{        
        Configuration conf=new Configuration();
        @SuppressWarnings("deprecation")
		Job job=new Job(conf);
        job.setJarByClass(Label.class);
        job.setReducerClass(LabelReducer.class);
        job.setMapperClass(LabelMapper.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0])); 
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        job.waitForCompletion(true);
	}
}
