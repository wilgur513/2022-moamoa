import { ReactNode } from 'react';

import Chip from '@components/Chip';

import * as S from './style';

export interface CardProps {
  thumbnailUrl: string;
  thumbnailAlt: string;
  title: string;
  description: string;
  extraChips: Array<ReactNode>;
}

const Card: React.FC<CardProps> = ({ thumbnailUrl, thumbnailAlt, title, description, extraChips }) => {
  return (
    <S.Card>
      <S.Image src={thumbnailUrl} alt={thumbnailAlt} />
      <S.Contents>
        <S.Title>{title}</S.Title>
        <S.Description>{description}</S.Description>
        <S.Extra>{extraChips}</S.Extra>
      </S.Contents>
    </S.Card>
  );
};

export default Card;
