package com.backend.lab.api.admin.member.agent.facade;

import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentListOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentSimpleResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.admin.PropertyRequest.mapper.PropertyRequestMapper;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentSearchOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAssignTicketReq;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminUpdateAgentItemReq;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentFullResp;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentFullResp.ItemWithTotalPrice;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentSearchResp;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentTicketsResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.agentItem.core.entity.AgentItem;
import com.backend.lab.domain.agentItem.core.entity.AgentTicket;
import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.domain.agentItem.core.service.AgentItemService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.memberNote.entity.dto.resp.MemberNoteResp;
import com.backend.lab.domain.memberNote.service.MemberNoteService;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import com.backend.lab.domain.purchase.core.entity.Purchase;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import com.backend.lab.domain.purchase.core.service.PurchaseService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAgentFacade {

  private final AgentService agentService;
  private final AgentItemService agentItemService;
  private final MemberNoteService memberNoteService;
  private final MemberGlobalFacade memberGlobalFacade;
  private final AdminService adminService;
  private final UploadFileService uploadFileService;
  private final PropertyRequestService propertyRequestService;
  private final PropertyRequestMapper propertyRequestMapper;
  private final PurchaseService purchaseService;
  private final MemberService memberService;

  public PageResp<AdminAgentSearchResp> search(AdminAgentSearchOptions options) {
    Page<Member> page = agentService.adminSearch(options);
    List<AdminAgentSearchResp> data = page.getContent().stream()
        .map(this::searchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public AdminAgentFullResp getById(Long id) {
    Member member = agentService.getById(id);
    return this.adminAgentFullResp(member);
  }

  public AdminAgentSearchResp searchResp(Member member) {

    if (member.getDeletedAt() != null) {
      return AdminAgentSearchResp.builder()
          .id(member.getId())
          .createdAt(member.getCreatedAt())
          .deletedAt(member.getDeletedAt())
          .status(AgentTicketStatus.DELETED)
          .build();
    }

    AgentItem item = agentItemService.getByAgent(member);
    AgentTicket currentTicket = agentItemService.getCurrentTicket(item.getTickets());

    AgentProperties agentProperties = member.getAgentProperties();
    AgentTicketStatus status = agentItemService.getTicketStatus(item);

    return AdminAgentSearchResp.builder()
        .id(member.getId())
        .email(member.getEmail())
        .name(agentProperties.getName())
        .businessName(agentProperties.getBusinessName())
        .phoneNumber(agentProperties.getPhoneNumber())

        .ticketName(currentTicket == null ? null : currentTicket.getName())
        .ticketStartAt(currentTicket == null ? null : currentTicket.getTicketStartAt())
        .ticketEndAt(currentTicket == null ? null : currentTicket.getTicketEndAt())

        .showContactCount(item.getShowContactCount())
        .advertiseCount(item.getAdvertiseCount())

        .createdAt(member.getCreatedAt())

        .address(agentProperties != null ? agentProperties.getAddress() : null)
        .addressDetail(agentProperties != null ? agentProperties.getAddressDetail() : null)

        .status(status)

        .build();
  }

  public AdminAgentFullResp adminAgentFullResp(Member member) {

    AgentItem item = agentItemService.getByAgent(member);
    AgentTicketStatus status = agentItemService.getTicketStatus(item);

    List<PropertyRequestAgentResp> properties = propertyRequestService.getsByMember(member).stream()
        .map(propertyRequestMapper::propertyRequestAgentResp)
        .toList();

    List<MemberNoteResp> notes = memberNoteService.getsByMember(member).stream()
        .map(m -> {
          Admin admin = adminService.getById(m.getAdminId());
          AdminResp adminResp = adminService.adminResp(admin,
              uploadFileService.uploadFileResp(admin.getProfileImage()));
          return memberNoteService.memberNoteResp(m, adminResp);
        })
        .toList();

    Integer totalPrice = 0;
    List<Purchase> purchaseItems = purchaseService.getsByAgent(member);
    for (Purchase pch : purchaseItems) {
      if (pch.getStatus().equals(PurchaseStatus.SUCCESS)) {
        totalPrice += pch.getTotalPrice();
      }
    }

    UploadFile profileImage = member.getProfileImage();
    UploadFileResp uploadFileResp = uploadFileService.uploadFileResp(profileImage);

    return AdminAgentFullResp.builder()
        .id(member.getId())
        .createdAt(member.getCreatedAt())
        .status(status)

        .memberResp(memberService.memberResp(member, uploadFileResp))

        .agentPropsResp(memberGlobalFacade.agentPropsResp(member))
        .item(ItemWithTotalPrice.builder()
            .item(agentItemService.agentMyItemResp(item))
            .totalPrice(totalPrice)
            .build())
        .propertyRequests(properties)
        .notes(notes)

        .build();
  }

  public PageResp<AdminAgentTicketsResp> getTickets(Long id, PageOptions options) {
    Member agent = agentService.getById(id);
    AgentItem agentItem = agentItemService.getByAgent(agent);
    Set<AgentTicket> tickets = agentItem.getTickets();

    Pageable pageable = options.pageable();
    int page = pageable.getPageNumber();
    int size = pageable.getPageSize();

    List<AdminAgentTicketsResp> data = tickets.stream()
        .filter(Objects::nonNull)
        .sorted(Comparator.nullsLast(Comparator.comparing(AgentTicket::getTicketStartAt)))
        .sorted(Comparator.nullsLast(Comparator.comparing(AgentTicket::getTicketEndAt)))
        .skip((long) page * size)
        .limit(size)
        .map(at -> AdminAgentTicketsResp.builder()
            .name(at.getName())
            .ticketStartAt(at.getTicketStartAt())
            .ticketEndAt(at.getTicketEndAt())
            .status(agentItemService.getTicketStatus(at))
            .build())
        .toList();

    long total = (long) tickets.size();
    int totalPage = data.size() / size;

    return new PageResp<>(page, size,totalPage,total,data);
  }

  @Transactional
  public void assignTicket(Long agentId, AdminAssignTicketReq req) {
    Member agent = agentService.getById(agentId);
    agentItemService.assignTicket(
        agent,
        req.getTicketName(),
        req.getTicketStartAt(),
        req.getTicketEndAt()
    );
  }

  @Transactional
  public void updateAgentItem(Long agentId, AdminUpdateAgentItemReq req) {
    Member agent = agentService.getById(agentId);
    agentItemService.updateAgentItemCounts(
        agent,
        req.getShowContactCount(),
        req.getAdvertiseCount()
    );
  }

  public List<AdminAgentSimpleResp> getAllActiveAgents() {
    List<Member> agents = agentService.getAllActiveAgents();
    return agents.stream()
        .map(agent -> {
          AgentProperties props = agent.getAgentProperties();
          return AdminAgentSimpleResp.builder()
              .id(agent.getId())
              .email(agent.getEmail())
              .name(props != null ? props.getName() : null)
              .businessName(props != null ? props.getBusinessName() : null)
              .phoneNumber(props != null ? props.getPhoneNumber() : null)
              .address(props != null ? props.getAddress() : null)
              .addressDetail(props != null ? props.getAddressDetail() : null)
              .build();
        })
        .toList();
  }

  public PageResp<AdminAgentSimpleResp> listActiveAgents(
      AdminAgentListOptions options) {
    Page<Member> page = agentService.listActiveAgents(options);
    List<AdminAgentSimpleResp> data = page.getContent()
        .stream()
        .map(agent -> {
          AgentProperties props = agent.getAgentProperties();
          return AdminAgentSimpleResp.builder()
              .id(agent.getId())
              .email(agent.getEmail())
              .name(props != null ? props.getName() : null)
              .businessName(props != null ? props.getBusinessName() : null)
              .phoneNumber(props != null ? props.getPhoneNumber() : null)
              .address(props != null ? props.getAddress() : null)
              .addressDetail(props != null ? props.getAddressDetail() : null)
              .build();
        })
        .toList();
    return new PageResp<>(page, data);
  }
}
