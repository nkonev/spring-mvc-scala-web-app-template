package name.dargiri.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMethod, PathVariable, RequestMapping}
import org.springframework.web.servlet.ModelAndView
import name.dargiri.web.util.spring.LayoutModelAndView
import name.dargiri.data.dto.PersonDTO
import java.util.UUID
import name.dargiri.data.service.PersonService
import org.springframework.beans.factory.annotation.Autowired
import name.dargiri.web.controller.MainController.PersonForm
import scala.beans.BeanProperty

object MainController {
  class PersonForm {
    @BeanProperty
    var id: UUID = _
    @BeanProperty
    var username: String = _
  }
}

/**
 * Created by dionis on 2/2/14.
 */
@Controller
@RequestMapping(value = Array(""))
class MainController {
  @Autowired
  var personService: PersonService = _

  @RequestMapping(value = Array("/", "/all"))
  def home(): ModelAndView = {
    val mav = new LayoutModelAndView("main/people")
    val all: Seq[PersonDTO] = personService.findAll
    mav.addObject("people", all)
  }

  @RequestMapping(value = Array("/create"))
  def createForm(): ModelAndView = {
    val mav = new LayoutModelAndView("main/person")
    mav.addObject("person", new PersonDTO)
    mav
  }

  @RequestMapping(value = Array("/create"), method = Array(RequestMethod.POST))
  def create(personForm: PersonForm): ModelAndView = {
    personService.create(toDTO(personForm))
    new ModelAndView("redirect:/all")
  }


  def toDTO(personForm: MainController.PersonForm): PersonDTO = {
    PersonDTO(personForm.id, personForm.username)
  }

  @RequestMapping(value = Array("/edit/{id}"))
  def editForm(@PathVariable("id") id: UUID): ModelAndView = {
    personService.find(id) match {
      case Some(person) => {
        val mav = new LayoutModelAndView("main/person")
        mav.addObject("person", person)
        mav
      }
      case _ => new ModelAndView("redirect:/")
    }
  }


  @RequestMapping(value = Array("/edit/{id}"), method = Array(RequestMethod.POST))
  def edit(personForm: PersonForm): ModelAndView = {
    val person: PersonDTO = toDTO(personForm)
    personService.update(person) match {
      case Some(_) => new ModelAndView("redirect:/")
      case _ => {
        val mav = new LayoutModelAndView("main/person")
        mav.addObject("person", person)
      }
    }
  }

  @RequestMapping(value = Array("/delete/{id}"))
  def delete(@PathVariable("id") id: UUID): ModelAndView = {
    personService.delete(id)
    new ModelAndView("redirect:/")
  }
}
