import { useState } from 'react';
import { useParams } from 'react-router-dom';

import ReviewTabPanel from '@study-room-page/components/review-tab-panel/ReviewTabPanel';

export type TabId = 'notice' | 'material' | 'review';

export type Tab = { id: TabId; name: string; content: React.ReactNode };

export type Tabs = Array<Tab>;

const useStudyRoomPage = () => {
  const { studyId } = useParams() as { studyId: string };
  // TODO: 내 스터디인지 아닌지 확인하는 api가 필요

  const tabs: Tabs = [
    { id: 'notice', name: '공지사항', content: '공지사항입니다.' },
    { id: 'material', name: '자료실', content: '자료실입니다.' },
    { id: 'review', name: '후기', content: <ReviewTabPanel studyId={Number(studyId)} /> },
  ];

  const [activeTabId, setActiveTabId] = useState<TabId>(tabs[0].id);

  const activeTab = tabs.find(({ id }) => id === activeTabId) as Tab;

  const handleTabButtonClick = (id: TabId) => () => {
    setActiveTabId(id);
  };

  return {
    tabs,
    activeTab,
    handleTabButtonClick,
  };
};

export default useStudyRoomPage;
