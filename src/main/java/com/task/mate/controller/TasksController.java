package com.task.mate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TasksController {

	
	@GetMapping("/tasks/list")
	public ModelAndView tasksList(Model model,HttpServletRequest httpRequest) {
		model.addAttribute("request", httpRequest);

		return new ModelAndView("tasks_list");
	}

	@GetMapping("/tasks/categories")
	public ModelAndView createTasksCategories() {
		return new ModelAndView("create_task_categories");
	}
}
