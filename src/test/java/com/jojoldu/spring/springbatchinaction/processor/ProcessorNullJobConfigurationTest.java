package com.jojoldu.spring.springbatchinaction.processor;

import com.jojoldu.spring.springbatchinaction.TestJobLauncher;
import com.jojoldu.spring.springbatchinaction.transaction.domain.Student;
import com.jojoldu.spring.springbatchinaction.transaction.domain.Teacher;
import com.jojoldu.spring.springbatchinaction.transaction.domain.TeacherRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jojoldu@gmail.com on 20/08/2018
 * Blog : http://jojoldu.tistory.com
 * Github : https://github.com/jojoldu
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessorNullJobConfigurationTest {

    @Autowired
    @Qualifier(ProcessorNullJobConfiguration.JOB_NAME)
    private Job job;

    @Autowired
    private TestJobLauncher testJobLauncher;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ProcessorNullJobConfiguration.TeacherStore teacherStore;

    @SuppressWarnings("Duplicates")
    @Test
    public void Process에서_NULL을_반환하면_Writer로_전달되지않는다() throws Exception {
        //given
        for(long i=0;i<10;i++) {
            String teacherName = i + "선생님";
            Teacher teacher = new Teacher(teacherName, "수학");
            Student student = new Student(teacherName+"의 제자");
            teacher.addStudent(student);
            teacherRepository.save(teacher);
        }

        JobLauncherTestUtils jobLauncherTestUtils = testJobLauncher.getJobLauncherTestUtils(job);
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("version", "1");

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(builder.toJobParameters());

        //then
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
        assertThat(teacherStore.getTeachers().size(), is(5));
    }

}
