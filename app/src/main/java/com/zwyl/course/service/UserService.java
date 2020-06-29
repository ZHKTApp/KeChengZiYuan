package com.zwyl.course.service;

import com.zwyl.course.dialog.bean.BeanAllYear;
import com.zwyl.course.dialog.bean.PopClassBean;
import com.zwyl.course.dialog.bean.PopSubjectBean;
import com.zwyl.course.http.HttpResult;
import com.zwyl.course.main.BeanHomeGrid;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    /**
     * 1.查学生全部科目接口.
     * 调用接口：/onlineCourse/selectAllSubjects
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * <p>
     * 返回list：返回多个参数，只有以下两个有用
     * 名称	字段命名	说明
     * 科目id	schoolSubjectId
     * 科目名称	schoolSubjectName
     **/
    @FormUrlEncoded
    @POST("onlineCourse/selectAllSubjects")
    Observable<HttpResult<List<PopSubjectBean>>> selectAllSubjects(@Field("nullStr") String nullStr);

    /**
     * 2.查学生所在学校全部年级接口.
     * 调用接口：/onlineCourse/selectAllSchoolGrade
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * <p>
     * 返回list：返回多个参数，只有以下两个有用
     * 名称	字段命名	说明
     * 年级id	schoolEducationGradeId
     * 年级名称	schoolEducationGradeName
     **/
    @FormUrlEncoded
    @POST("onlineCourse/selectAllSchoolGrade")
    Observable<HttpResult<List<PopClassBean>>> selectAllSchoolGrade(@Field("nullStr") String nullStr);

    /**
     * 3.查网络课程接口接口.
     * 调用接口：/onlineCourse/selectOnlineStudyBook
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * 科目id	schoolSubjectId	可为空
     * 年级id	schoolEducationGradeId	可为空
     * 网络课程名称	onlineStudyName	可为空
     * 收藏标识	collectType	collectType等于0的话查的是所有的资源，等于1查的是收藏的资源
     **/
    @FormUrlEncoded
    @POST("onlineCourse/selectCourseResource")
    Observable<HttpResult<List<BeanHomeGrid>>> selectCourseResource(@Field("schoolSubjectId") String schoolSubjectId, @Field("schoolEducationGradeId") String schoolEducationGradeId, @Field("onlineStudyName") String resourceName, @Field("collectType") String collectType, @Field("sourceType") String sourceType);

    /**
     * 5.课程资源记录下载状态接口,网络课程和电子书下载共用此接口
     * 调用接口：/onlineCourse/downloadCourseResource
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * 资源id(网络课程或电子书id)	sourceId	可为空
     **/
    @FormUrlEncoded
    @POST("onlineCourse/downloadCourseResource")
    Observable<HttpResult<String>> downloadCourseResource(@Field("sourceId") String sourceId);

    /**
     * 6.课程资源记录收藏状态接口,网络课程和电子书收藏共用此接口
     * 调用接口：/onlineCourse/collectCourseResource
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * 资源id(网络课程或电子书id)	sourceId	可为空
     **/
    @FormUrlEncoded
    @POST("onlineCourse/collectCourseResource")
    Observable<HttpResult<String>> collectCourseResource(@Field("sourceId") String sourceId);

    /**
     * 7.取消收藏状态接口
     * 调用接口：/onlineCourse/cancelCollect
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * 资源id(网络课程或电子书id)	sourceId	可为空
     **/
    @FormUrlEncoded
    @POST("onlineCourse/cancelCollect")
    Observable<HttpResult<String>> cancelCollect(@Field("sourceId") String sourceId);

    /**
     * 8.资源观看人数接口
     * 资源id    resourceId
     * 资源类型	type
     * 学生	Token
     * 老师id	cmTeacherId
     * 课本id	textBookId
     */
    @FormUrlEncoded
    @POST("homework/addlog")
    Observable<HttpResult<String>>addlog(@Field("resourceId") String resourceId,@Field("type") String type,
                                         @Field("cmTeacherId") String cmTeacherId,
                                         @Field("textBookId") String textBookId);
}