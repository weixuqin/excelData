package com.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pojo.User;
import com.service.UserService;
import com.utils.MakeExcel;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

@Controller
public class ExcleData {
	@Autowired
	private UserService userService;

	/**
	 * 下载用户 excel 表接口
	 * 
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/download")
	public void download(HttpServletResponse response) throws IOException {
		// 指定一个地方临时存放生成的 excel 文件，然后后面调用浏览器接口下载完后再删除
		String FILEPATH = "d:/test.xls";
		// 判断 "c:/test.xls" 文件是否已经存在，如果存在就删除掉
		MakeExcel.deleteFile(FILEPATH);
		// 首行表头信息
		List<String> ll = new ArrayList<>();
		ll.add("用户ID");
		ll.add("姓名");
		ll.add("电话");
		// 获取所有用户信息
		List<User> allUserList = userService.getUserList();
		// 将用户的相关信息遍历到 List<Map<String, Object>> 中
		List<Map<String, Object>> list = new ArrayList<>();
		for (User user : allUserList) {
			Map<String, Object> map = new HashMap<>();

			map.put("用户ID", user.getId());
			map.put("姓名", user.getName());
			map.put("电话", user.getPhone());
			list.add(map);
		}
		try {
			// 第一个参数：表格中的数据
			// 第二个参数：表格保存的路径
			// 第三个参数：表格第二行的列信息
			// 第四个参数：表格第一行的表头信息
			// 参照效果图看会清楚些
			MakeExcel.CreateExcelFile(list, new File(FILEPATH), ll, "用户表");
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 调用浏览器下载接口
		MakeExcel.send(FILEPATH, response);
		// 删除临时存放的 excel 文件
		boolean deleteFileState = MakeExcel.deleteFile(FILEPATH);
		if (deleteFileState) {
			System.out.println("服务器上文件删除成功！！！");
		} else {
			System.out.println("服务器上文件删除失败！！！");
		}
	}

	/**
	 * 从 excel 中添加数据到数据库中
	 * 
	 * @param filename
	 * @param request
	 * @throws IOException
	 * @throws BiffException
	 */
	@RequestMapping(value = "/getexcelfile", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getExcelFile(MultipartFile file, HttpServletRequest request) throws IOException, BiffException {
		/** 临时存放 excel 文件 **/
		// 设置相对路径
		String realPath = request.getSession().getServletContext().getRealPath("/upload");
		System.out.println(" realPath:" + realPath);
		// 存放 excel 文件的绝对路径
		String uploadPath = "";
		// 获取文件的格式
		String extention = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
		// 对格式进行筛选
		// 仅支持上传 excel 文件，xls 或 xlsx
		if (extention.equalsIgnoreCase("xls") || extention.equalsIgnoreCase("xlsx")) {
			File f = new File(realPath);// 在路径下创建文件夹
			String fileName = file.getOriginalFilename();// 获取上传文件原命名
			uploadPath = realPath + File.separator + fileName;// 拼接上传路径
			System.out.println(" uploadPath:" + uploadPath);
			// 如果指定文件 upload 不存在，则先新建文件夹
			if (!f.exists()) {
				f.mkdirs();
			}
			file.transferTo(new File(uploadPath));// 文件的传输
		} else {
			System.out.println("上传文件格式不对！");
		}

		/** 读取临时存放的 excel 文件，并将数据导入数据库 **/
		// 获取 excel 表的文件流
		File excelFile = new File(uploadPath);
		Workbook book = Workbook.getWorkbook(excelFile);
		// 获取 sheet 表
		// 可以使用下面的写法，"test0" 表示 sheet 表的名字
		// Sheet rs = book.getSheet("test0");
		// getSheet(0):表示获取第一张 sheet 表（从左到右数起）
		Sheet rs = book.getSheet(0);
		int columns = rs.getColumns();// 得到所有的列
		int rows = rs.getRows();// 得到所有的行
		System.out.println(" columns:" + columns + " rows:" + rows);
		List<User> list = new ArrayList<>();// 临时存放 excel 数据
		/** 将 excel 数据存放到 List<User> **/
		// i=2:表示第三行
		for (int i = 2; i < rows; i++) {
			User user = new User();
			// j=0:表示第一列
			// excel 中默认左边编号也算一列，所以需要从第二列开始获取数据，则下面都使用 j++
			for (int j = 0; j < columns; j++) {
				String id = rs.getCell(j++, i).getContents();
				String name = rs.getCell(j++, i).getContents();
				String phone = rs.getCell(j++, i).getContents();
				System.out.println("id=" + id);
				System.out.println("name=" + name);
				System.out.println("phone=" + phone);
				// 将数据 set 进 user 对象中
				user.setId(Integer.parseInt(id));
				user.setName(name);
				user.setPhone(phone);
				// 将 user 对象添加到 List<User> 里
				list.add(user);
			}
		}
		/** 将 List<User> 中的数据插入或者更新到数据库 **/
		User userOne = new User();
		User tempUser = new User();
		for (int k = 0; k < list.size(); k++) {
			userOne = list.get(k);
			tempUser = userService.selectUserById(userOne.getId());
			if (tempUser != null) {
				// 查询返回值 tempUser 不为空，则说明当前这条信息已经存在数据库
				// 进行数据更新操作即可
				userService.updataUserByKey(userOne);
			} else if (tempUser == null) {
				// tempUser 为空，数据库没有当前信息
				// 将数据插入即可
				userService.insertUser(userOne);
			}
		}

		/** 删除临时存放的 excel 文件 **/
		boolean deleteFileState = MakeExcel.deleteFile(realPath + "/test.xls");
		if (deleteFileState) {
			System.out.println("服务器上文件删除成功！！！");
		} else {
			System.out.println("服务器上文件删除失败！！！");
		}
		return " excel 数据更新到数据库成功";
	}
}
